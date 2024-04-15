package co.touchlab.kmmbridgekickstart

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.touchlab.kmmbridgekickstart.db.KMMBridgeKickStartDb
import co.touchlab.kmmbridgekickstart.repository.BreedRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal fun breedStartup(context: Context, analyticsHandle: AnalyticsHandle): BreedRepository {
    val locator = AndroidServiceLocator(context, analyticsHandle)
    return locator.breedRepository
}

internal class AndroidServiceLocator(
    context: Context,
    analyticsHandle: AnalyticsHandle
) : BaseServiceLocator(analyticsHandle) {

    override val sqlDriver: SqlDriver by lazy {
        AndroidSqliteDriver(
            schema = KMMBridgeKickStartDb.Schema,
            context = context,
            name = DB_NAME
        )
    }

    override val settings: Settings by lazy {
        SharedPreferencesSettings(
            delegate = context.getSharedPreferences(
                SETTINGS_KEY,
                Context.MODE_PRIVATE
            )
        )
    }

    override val clientEngine: HttpClientEngine by lazy { OkHttp.create() }
}
