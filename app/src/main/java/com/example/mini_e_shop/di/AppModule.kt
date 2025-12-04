package com.example.mini_e_shop.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mini_e_shop.data.local.SampleData
import com.example.mini_e_shop.data.local.dao.CartDao
import com.example.mini_e_shop.data.local.dao.OrderDao
import com.example.mini_e_shop.data.local.dao.ProductDao
import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.database.AppDatabase
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.entity.UserRole
import com.example.mini_e_shop.data.repository.CartRepositoryImpl
import com.example.mini_e_shop.data.repository.OrderRepositoryImpl
import com.example.mini_e_shop.data.repository.ProductRepositoryImpl
import com.example.mini_e_shop.data.repository.UserRepositoryImpl
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.OrderRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Provider
import javax.inject.Singleton

// --- MODULE CUNG CẤP CÁC ĐỐI TƯỢNG CỤ THỂ (DATABASE, DAO) ---
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule { // Đổi tên thành DatabaseModule cho rõ ràng

    @Provides
    @Singleton
    fun provideAppDatabase(
        app: Application,
        dbProvider: Provider<AppDatabase>
    ): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "mini_e_shop.db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val userDao = dbProvider.get().userDao()
                        val productDao = dbProvider.get().productDao()

                        // Tạo tài khoản Admin
                        val adminPasswordHash = BCrypt.hashpw("admin123", BCrypt.gensalt())
                        val adminAccount = UserEntity(
                            email = "admin@eshop.com",
                            passwordHash = adminPasswordHash,
                            name = "Admin",
                            role = UserRole.ADMIN
                        )
                        userDao.insertUser(adminAccount)

                        // Thêm sản phẩm mẫu
                        productDao.insertProducts(SampleData.getSampleProducts())
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }

    // Cung cấp các DAO từ Database
    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()

    @Provides
    @Singleton
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
}

// --- MODULE LIÊN KẾT (BINDING) INTERFACE VỚI CLASS TRIỂN KHAI ---
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // 1. SỬ DỤNG @Binds ĐỂ LIÊN KẾT
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}

// Dòng code 'data class AppDatabaseDaos(...)' và các hàm provider cũ có thể được xóa bỏ
