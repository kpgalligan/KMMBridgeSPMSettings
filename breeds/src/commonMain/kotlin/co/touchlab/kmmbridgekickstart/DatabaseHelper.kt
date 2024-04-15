package co.touchlab.kmmbridgekickstart

import app.cash.sqldelight.coroutines.asFlow
import co.touchlab.kmmbridgekickstart.db.Breed
import co.touchlab.kmmbridgekickstart.db.KMMBridgeKickStartDb
import co.touchlab.kmmbridgekickstart.sqldelight.transactionWithContext
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

internal class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val breedAnalytics: BreedAnalytics,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: KMMBridgeKickStartDb = KMMBridgeKickStartDb(sqlDriver)

    fun selectAllItems(): Flow<List<Breed>> =
        dbRef.tableQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .flowOn(backgroundDispatcher)

    suspend fun insertBreeds(breeds: List<String>) {
        breedAnalytics.insertingBreedsToDatabase(breeds.size)
        dbRef.transactionWithContext(backgroundDispatcher) {
            breeds.forEach { breed ->
                dbRef.tableQueries.insertBreed(breed)
            }
        }
    }

    fun selectById(id: Long): Flow<List<Breed>> =
        dbRef.tableQueries
            .selectById(id)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .flowOn(backgroundDispatcher)

    suspend fun deleteAll() {
        breedAnalytics.databaseCleared()
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.deleteAll()
        }
    }

    suspend fun updateFavorite(breedId: Long, favorite: Boolean) {
        breedAnalytics.favoriteSaved(id = breedId, favorite = favorite)
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.updateFavorite(favorite, breedId)
        }
    }
}
