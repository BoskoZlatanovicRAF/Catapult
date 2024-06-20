package raf.rs.rma_projekat.user.profile

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ProfileDataStore @Inject constructor(
    private val dataStore: DataStore<ProfileData>
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    val data = dataStore.data
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { dataStore.data.first() },
        )

    val dataFlow = dataStore.data

    suspend fun updateProfileData(data: ProfileData) {
        dataStore.updateData { data }
    }

    suspend fun updateNickname(nickname: String): ProfileData {
        return dataStore.updateData {
            it.copy(nickname = nickname)
        }
    }

    suspend fun updateFullName(fullName: String): ProfileData {
        return dataStore.updateData {
            it.copy(fullName = fullName)
        }
    }

    suspend fun updateEmail(email: String): ProfileData {
        return dataStore.updateData {
            it.copy(email = email)
        }
    }


    suspend fun clearProfileData() {
        dataStore.updateData { ProfileData.EMPTY }
    }
}
