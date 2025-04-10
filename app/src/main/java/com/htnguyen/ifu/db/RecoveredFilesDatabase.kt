package com.htnguyen.ifu.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.htnguyen.ifu.model.RecoveredFile
import java.util.*

/**
 * Cơ sở dữ liệu để lưu trữ thông tin về các tệp tin đã được khôi phục
 */
class RecoveredFilesDatabase private constructor(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Tạo bảng khi cơ sở dữ liệu được tạo lần đầu
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PATH TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_SIZE INTEGER NOT NULL,
                $COLUMN_MODIFIED_DATE INTEGER NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_RECOVERY_DATE INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    /**
     * Nâng cấp cơ sở dữ liệu khi phiên bản thay đổi
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Trong phiên bản đầu tiên, chỉ cần xóa bảng cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * Thêm một tệp tin đã khôi phục vào cơ sở dữ liệu
     * @param file Tệp tin đã khôi phục
     * @param type Loại tệp tin (ảnh, video, tệp tin khác)
     * @return ID của bản ghi mới, hoặc -1 nếu thêm thất bại
     */
    fun addRecoveredFile(file: RecoveredFile, type: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PATH, file.path)
            put(COLUMN_NAME, file.name)
            put(COLUMN_SIZE, file.size)
            put(COLUMN_MODIFIED_DATE, file.modifiedDate.time)
            put(COLUMN_TYPE, type)
            put(COLUMN_RECOVERY_DATE, System.currentTimeMillis())
        }

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        
        return id
    }

    /**
     * Lấy tất cả các tệp tin đã khôi phục theo loại
     * @param type Loại tệp tin (ảnh, video, tệp tin khác), hoặc null để lấy tất cả
     * @return Danh sách các tệp tin đã khôi phục
     */
    fun getRecoveredFiles(type: String? = null): List<RecoveredFile> {
        val files = mutableListOf<RecoveredFile>()
        val db = readableDatabase
        
        val selection = if (type != null) "$COLUMN_TYPE = ?" else null
        val selectionArgs = if (type != null) arrayOf(type) else null
        val orderBy = "$COLUMN_RECOVERY_DATE DESC"
        
        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            orderBy
        )
        
        if (cursor.moveToFirst()) {
            do {
                val file = cursorToRecoveredFile(cursor)
                files.add(file)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        
        return files
    }

    /**
     * Đếm số lượng tệp tin đã khôi phục theo loại
     * @param type Loại tệp tin (ảnh, video, tệp tin khác), hoặc null để đếm tất cả
     * @return Số lượng tệp tin
     */
    fun countRecoveredFiles(type: String? = null): Int {
        val db = readableDatabase
        val selection = if (type != null) "$COLUMN_TYPE = ?" else null
        val selectionArgs = if (type != null) arrayOf(type) else null
        
        val cursor = db.query(
            TABLE_NAME,
            arrayOf("COUNT(*) AS count"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        
        cursor.close()
        db.close()
        
        return count
    }

    /**
     * Tính tổng kích thước các tệp tin đã khôi phục theo loại
     * @param type Loại tệp tin (ảnh, video, tệp tin khác), hoặc null để tính tất cả
     * @return Tổng kích thước (byte)
     */
    fun getTotalSize(type: String? = null): Long {
        val db = readableDatabase
        val selection = if (type != null) "$COLUMN_TYPE = ?" else null
        val selectionArgs = if (type != null) arrayOf(type) else null
        
        val cursor = db.query(
            TABLE_NAME,
            arrayOf("SUM($COLUMN_SIZE) AS total_size"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        var totalSize = 0L
        if (cursor.moveToFirst()) {
            totalSize = cursor.getLong(0)
        }
        
        cursor.close()
        db.close()
        
        return totalSize
    }

    /**
     * Kiểm tra xem một tệp tin đã tồn tại trong cơ sở dữ liệu chưa
     * @param path Đường dẫn đến tệp tin
     * @return true nếu tệp tin đã tồn tại, false nếu chưa
     */
    fun fileExists(path: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_PATH = ?"
        val selectionArgs = arrayOf(path)
        
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        
        return exists
    }

    /**
     * Xóa một tệp tin khỏi cơ sở dữ liệu
     * @param path Đường dẫn đến tệp tin
     * @return Số lượng bản ghi bị xóa
     */
    fun deleteFile(path: String): Int {
        val db = writableDatabase
        val selection = "$COLUMN_PATH = ?"
        val selectionArgs = arrayOf(path)
        
        val count = db.delete(TABLE_NAME, selection, selectionArgs)
        db.close()
        
        return count
    }

    /**
     * Chuyển đổi từ Cursor sang đối tượng RecoveredFile
     */
    private fun cursorToRecoveredFile(cursor: Cursor): RecoveredFile {
        val pathIndex = cursor.getColumnIndex(COLUMN_PATH)
        val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
        val sizeIndex = cursor.getColumnIndex(COLUMN_SIZE)
        val modifiedDateIndex = cursor.getColumnIndex(COLUMN_MODIFIED_DATE)
        
        val path = cursor.getString(pathIndex)
        val name = cursor.getString(nameIndex)
        val size = cursor.getLong(sizeIndex)
        val modifiedDate = Date(cursor.getLong(modifiedDateIndex))
        
        return RecoveredFile(path, name, size, modifiedDate, false)
    }

    companion object {
        private const val DATABASE_NAME = "recovered_files.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "recovered_files"
        
        private const val COLUMN_ID = "id"
        private const val COLUMN_PATH = "path"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_SIZE = "size"
        private const val COLUMN_MODIFIED_DATE = "modified_date"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_RECOVERY_DATE = "recovery_date"
        
        // Các loại tệp tin
        const val TYPE_PHOTO = "photo"
        const val TYPE_VIDEO = "video"
        const val TYPE_FILE = "file"
        
        // Singleton instance
        @Volatile
        private var INSTANCE: RecoveredFilesDatabase? = null
        
        fun getInstance(context: Context): RecoveredFilesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = RecoveredFilesDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
} 