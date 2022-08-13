package com.zabster.safeappcompose.db.helper.db

import android.content.Context
import androidx.annotation.MainThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.zabster.safeappcompose.db.AppDatabase
import com.zabster.safeappcompose.db.AppDatabaseException
import com.zabster.safeappcompose.db.helper.secure.AppSecureUtils
import com.zabster.safeappcompose.db.prefs.PreferencesKeys
import com.zabster.safeappcompose.di.dispatchers.DispatchersHelper
import com.zabster.safeappcompose.di.res.ResourceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class AppDatabaseHelperImpl(
    private val context: Context,
    private val resourceManager: ResourceManager,
    private val dispatchersHelper: DispatchersHelper,
    private val dataStore: DataStore<Preferences>,
    private val appSecureUtils: AppSecureUtils
) : AppDatabaseHelper {

    override val database: AppDatabase
        get() = requireNotNull(_database) {
            "Data base not created. Call initialize first"
        }

    override val wasInit: Boolean
        get() = _database != null

    private var _database: AppDatabase? = null

    override suspend fun initialize() = withContext(dispatchersHelper.main) {
        if (_database != null) throw AppDatabaseException.DatabaseAlreadyCreating
        val data = dataStore.data.first()
        val pass = data[PreferencesKeys.PasswordKey]
        val decryptPass = appSecureUtils.decrypt(pass.orEmpty())
        _database =
            Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                .openHelperFactory(SupportFactory(SQLiteDatabase.getBytes(decryptPass.toCharArray())))
                .addCallback(DatabaseCreationCallback(resourceManager))
                .build()
    }
}