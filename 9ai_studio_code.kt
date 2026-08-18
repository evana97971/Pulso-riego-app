package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.dao.ConfigDao
import com.example.data.local.dao.LotDao
import com.example.data.local.dao.PulseDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ConfigEntity
import com.example.data.local.entity.LotEntity
import com.example.data.local.entity.PulseEntity
import com.example.data.local.entity.UserEntity
import com.example.model.AppUser
import com.example.model.Lot
import com.example.model.MonitoringState
import com.example.model.Pulse
import com.example.model.Thresholds
import com.example.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PulseRepository(
  private val database: AppDatabase,
  private val repositoryScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
  private val lotDao: LotDao = database.lotDao()
  private val pulseDao: PulseDao = database.pulseDao()
  private val userDao: UserDao = database.userDao()
  private val configDao: ConfigDao = database.configDao()

  val defaultUsers = listOf(
    AppUser(
      id = "usr-1",
      username = "admin",
      fullName = "Super Administrador",
      passwordHash = "052707Jp@",
      role = UserRole.ADMIN,
      email = "evana97971@gmail.com",
      phone = "+51 987 654 321"
    ),
    AppUser(
      id = "usr-2",
      username = "ing_riego",
      fullName = "Ing. Carlos Mendoza",
      passwordHash = "riego2026",
      role = UserRole.ING_RIEGO,
      email = "carlos.riego@agricola.com",
      phone = "+51 912 345 678"
    ),
    AppUser(
      id = "usr-3",
      username = "regador1",
      fullName = "Pedro Quispe",
      passwordHash = "agua2026",
      role = UserRole.REGADOR,
      email = "pedro.q@agricola.com",
      phone = "+51 985 556 677"
    )
  )

  val defaultLots = listOf(
    Lot(id = "RA1", avgDrenaje = 5.0, overrideRecommendation = "Aumentar volumen del siguiente pulso"),
    Lot(id = "RA2", avgDrenaje = 55.0, overrideRecommendation = "Reducir duración del siguiente pulso en 10%"),
    Lot(id = "RA3", avgDrenaje = 38.0),
    Lot(id = "RA4", avgDrenaje = null),
    Lot(id = "RA5", avgDrenaje = null),
    Lot(id = "RA6", avgDrenaje = null)
  )

  val defaultPulses = listOf(
    Pulse(
      id = 1,
      lote = "RA3",
      sfr_ml = 600.0,
      drenaje_pct = 38.0,
      inicio = "07:10",
      fin = "07:30"
    )
  )

  private val _state = MutableStateFlow(
    MonitoringState(
      pulses = defaultPulses,
      lots = defaultLots,
      users = defaultUsers,
      currentUser = defaultUsers.first(),
      thresholds = Thresholds(high = 40.0, low = 10.0)
    )
  )

  val state: StateFlow<MonitoringState> = _state.asStateFlow()

  init {
    repositoryScope.launch {
      seedDatabaseIfEmpty()
      observeRoomDatabase()
    }
  }

  private suspend fun seedDatabaseIfEmpty() {
    val currentConfig = configDao.getConfig()
    if (currentConfig == null) {
      configDao.saveConfig(
        ConfigEntity(
          id = 1,
          highThreshold = 40.0,
          lowThreshold = 10.0,
          currentUserId = "usr-1"
        )
      )
      userDao.insertUsers(defaultUsers.map { UserEntity.fromDomain(it) })
      lotDao.insertLots(defaultLots.map { LotEntity.fromDomain(it) })
      pulseDao.insertPulses(defaultPulses.map { PulseEntity.fromDomain(it) })
    }
  }

  private var isInitialSessionRestored = false

  private fun observeRoomDatabase() {
    repositoryScope.launch {
      combine(
        lotDao.getAllLots(),
        pulseDao.getAllPulses(),
        userDao.getAllUsers(),
        configDao.getConfigFlow()
      ) { lotEntities, pulseEntities, userEntities, configEntity ->
        val domainLots = if (lotEntities.isNotEmpty()) lotEntities.map { it.toDomain() } else _state.value.lots
        val domainPulses = if (pulseEntities.isNotEmpty()) pulseEntities.map { it.toDomain() } else _state.value.pulses
        val domainUsers = if (userEntities.isNotEmpty()) userEntities.map { it.toDomain() } else _state.value.users

        val high = configEntity?.highThreshold ?: _state.value.thresholds.high
        val low = configEntity?.lowThreshold ?: _state.value.thresholds.low
        val thresholds = Thresholds(high = high, low = low)

        val currentUser = if (!isInitialSessionRestored && configEntity?.currentUserId != null) {
          isInitialSessionRestored = true
          domainUsers.find { it.id == configEntity.currentUserId } ?: _state.value.currentUser
        } else {
          _state.value.currentUser
        }

        MonitoringState(
          pulses = domainPulses,
          lots = domainLots,
          users = domainUsers,
          currentUser = currentUser,
          thresholds = thresholds
        )
      }.collect { dbState ->
        _state.value = dbState
      }
    }
  }

  fun authenticate(usernameOrEmail: String, password: String): AppUser? {
    val cleanUser = usernameOrEmail.trim()
    val cleanPass = password.trim()
    
    val users = state.value.users.ifEmpty { defaultUsers }
    val user = users.find {
      (it.username.equals(cleanUser, ignoreCase = true) || it.email.equals(cleanUser, ignoreCase = true)) &&
        it.passwordHash == cleanPass &&
        it.active
    }

    if (user != null) {
      _state.update { it.copy(currentUser = user) }
      repositoryScope.launch {
        val currentCfg = configDao.getConfig() ?: ConfigEntity(id = 1)
        configDao.saveConfig(currentCfg.copy(currentUserId = user.id))
      }
    }
    return user
  }

  fun logout() {
    _state.update { it.copy(currentUser = null) }
    repositoryScope.launch {
      val currentCfg = configDao.getConfig() ?: ConfigEntity(id = 1)
      configDao.saveConfig(currentCfg.copy(currentUserId = null))
    }
  }

  fun addPulse(
    lote: String,
    sfrMl: Double,
    drenajePct: Double,
    inicio: String,
    fin: String
  ): Pulse {
    val nextId = (state.value.pulses.maxOfOrNull { it.id } ?: 0) + 1
    val newPulse = Pulse(
      id = nextId,
      lote = lote,
      sfr_ml = sfrMl,
      drenaje_pct = drenajePct,
      inicio = inicio,
      fin = fin
    )

    _state.update { current ->
      val updatedPulses = listOf(newPulse) + current.pulses
      val lotPulses = updatedPulses.filter { it.lote == lote }
      val computedAvg = if (lotPulses.isNotEmpty()) lotPulses.map { it.drenaje_pct }.average() else drenajePct
      
      val updatedLots = current.lots.map { lot ->
        if (lot.id == lote) lot.copy(avgDrenaje = computedAvg) else lot
      }
      current.copy(pulses = updatedPulses, lots = updatedLots)
    }

    repositoryScope.launch {
      pulseDao.insertPulse(PulseEntity.fromDomain(newPulse))
      val lot = lotDao.getLotById(lote)
      if (lot != null) {
        val allLotPulses = state.value.pulses.filter { it.lote == lote }
        val newAvg = if (allLotPulses.isNotEmpty()) allLotPulses.map { it.drenaje_pct }.average() else drenajePct
        lotDao.updateLot(lot.copy(avgDrenaje = newAvg))
      }
    }

    return newPulse
  }

  fun updateThresholds(high: Double, low: Double) {
    val newThresholds = Thresholds(high = high, low = low)
    _state.update { it.copy(thresholds = newThresholds) }
    repositoryScope.launch {
      val currentCfg = configDao.getConfig() ?: ConfigEntity(id = 1)
      configDao.saveConfig(currentCfg.copy(highThreshold = high, lowThreshold = low))
    }
  }
}