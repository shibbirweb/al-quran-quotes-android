package shibbir.me.alquranquotes.testing

import androidx.sqlite.db.SupportSQLiteDatabase
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * A [SupportSQLiteDatabase] for JVM tests that only records the statements passed to execSQL,
 * in order. Every other call does nothing and returns null, so it only suits code that writes
 * plain SQL, such as a Migration.
 */
class SqlRecordingDatabase {

    val executedSql = mutableListOf<String>()

    val database: SupportSQLiteDatabase = createRecordingProxy()

    private fun createRecordingProxy(): SupportSQLiteDatabase {
        val classLoader = SupportSQLiteDatabase::class.java.classLoader
        val implementedInterfaces = arrayOf(SupportSQLiteDatabase::class.java)
        val recordingHandler = InvocationHandler { _, calledMethod, methodArguments ->
            recordIfExecSql(calledMethod, methodArguments)
            null
        }
        val recordingProxy = Proxy.newProxyInstance(
            classLoader,
            implementedInterfaces,
            recordingHandler,
        )
        return recordingProxy as SupportSQLiteDatabase
    }

    private fun recordIfExecSql(calledMethod: Method, methodArguments: Array<out Any?>?) {
        if (calledMethod.name != "execSQL") {
            return
        }
        val sqlStatement = methodArguments?.firstOrNull() as String
        executedSql += sqlStatement
    }
}
