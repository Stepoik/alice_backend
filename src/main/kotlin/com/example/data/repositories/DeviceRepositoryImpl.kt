package com.example.data.repositories

import com.example.data.models.Devices
import com.example.domain.models.Device
import com.example.domain.models.DeviceState
import com.example.domain.repositories.DeviceRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DeviceRepositoryImpl(
    private val database: Database
) : DeviceRepository, BaseRepository<Devices>() {

    override suspend fun getDevicesByUserId(userId: String): List<Device> {
        return dbQuery {
            Devices.select { Devices.userId eq userId }.map(ResultRow::toDevice)
        }
    }

    override suspend fun getDevicesByIds(ids: List<Int>): List<Device> {
        return dbQuery { Devices.select { Devices.id inList ids }.map(ResultRow::toDevice) }
    }

    override suspend fun getDeviceById(id: Int): Device? {
        return dbQuery { Devices.select { Devices.id eq id }.map(ResultRow::toDevice).singleOrNull() }
    }

    override suspend fun addDevice(device: Device) {
        dbQuery {
            Devices.insert {
                it[id] = device.id
                it[userId] = device.userId
                it[name] = device.name
                it[description] = device.description
                it[type] = device.type
                it[isOn] = device.isOn
                it[temperature] = device.temperature
                it[secret] = device.secret
            }
        }
    }

    override suspend fun removeDevice(id: Int) {
        dbQuery { Devices.deleteWhere { Devices.id eq id } }
    }

    override suspend fun getDeviceState(id: Int): DeviceState? {
        return dbQuery {
            Devices.select {
                Devices.id eq id
            }.map {
                DeviceState(
                    temperature = it[Devices.temperature],
                    isOn = it[Devices.isOn]
                )
            }.firstOrNull()
        }
    }

    override suspend fun updateDeviceState(id: Int, deviceState: DeviceState) {
        dbQuery {
            Devices.update({ Devices.id eq id }) {
                it[temperature] = deviceState.temperature
                it[isOn] = deviceState.isOn
            }
        }
    }

    override suspend fun setUser(userId: String, deviceId: Int) {
        dbQuery {
            Devices.update( { Devices.id eq deviceId}) {
                it[Devices.userId] = userId
            }
        }
    }
}

private fun ResultRow.toDevice() = Device(
    id = this[Devices.id],
    name = this[Devices.name],
    description = this[Devices.description],
    type = this[Devices.type],
    userId = this[Devices.userId],
    isOn = this[Devices.isOn],
    temperature = this[Devices.temperature],
    secret = this[Devices.secret],
)