package com.mustfaibra.roffu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mustfaibra.roffu.R
import com.mustfaibra.roffu.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [
        Advertisement::class,
        Manufacturer::class,
        Review::class,
        User::class,
        PaymentProvider::class,
        UserPaymentProvider::class,
        Product::class,
        BookmarkItem::class,
        Location::class,
        CartItem::class,
        Order::class,
        OrderItem::class,
        OrderPayment::class,
        Notification::class,
        ProductColor::class,
        ProductSize::class,
    ],
    version = 1, exportSchema = false)
abstract class RoomDb : RoomDatabase() {

    /** A function that used to retrieve Room's related dao instance */
    abstract fun getDao(): RoomDao

    class PopulateDataClass @Inject constructor(
        private val client: Provider<RoomDb>,
        private val scope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        private val description =
            "This is the description text that is supposed to be long enough to show how the UI looks, so it's not a real text.\n"
        private val manufacturers = listOf(
            Manufacturer(id = 1, name = "For Family", icon = R.drawable.ic_round_family_restroom_24),
            Manufacturer(id = 2, name = "Single", icon = R.drawable.ic_baseline_directions_run_24),
            Manufacturer(id = 3, name = "Student", icon = R.drawable.ic_round_school_24),
            Manufacturer(id = 4, name = "Vacation", icon = R.drawable.ic_round_wb_sunny_24),
        )
        private val advertisements = listOf(
            Advertisement(1, R.drawable.image1, 1, 0),
            Advertisement(2, R.drawable.image2, 2, 0),
            Advertisement(3, R.drawable.image3, 3, 0),
            Advertisement(4, R.drawable.image4, 4, 0),
        )
        private val nikeProducts = listOf(
            Product(
                id = 1,
                name = "Whole 2 bedrooms apartment",
                image = R.drawable.image05,
                price = 20000.0,
                description = description,
                manufacturerId = 1,
                basicColorName = "dark-green",
            ).also {
                it.colors = mutableListOf(
                    ProductColor(productId = it.id,
                        colorName = it.basicColorName,
                        image = it.image),
                    ProductColor(productId = it.id,
                        colorName = "lemon",
                        image = R.drawable.image05),
                )
            },
            Product(
                id = 3,
                name = "Furnished 1-bedroom apartment",
                image = R.drawable.image06,
                price = 15000.0,
                description = description,
                manufacturerId = 1,
                basicColorName = "gold",
            ).also {
                it.colors = mutableListOf(
                    ProductColor(productId = it.id,
                        colorName = it.basicColorName,
                        image = it.image),
                    ProductColor(productId = it.id,
                        colorName = "gray",
                        image = R.drawable.image06),
                    ProductColor(productId = it.id,
                        colorName = "pink",
                        image = R.drawable.image06),
                    ProductColor(productId = it.id,
                        colorName = "red",
                        image = R.drawable.image06),
                )
            },
            Product(
                id = 7,
                name = "Practical and modern 1 bedroom apartment",
                image = R.drawable.image07,
                price = 32000.0,
                description = description,
                manufacturerId = 1,
                basicColorName = "black",
            ).also {
                it.colors = mutableListOf(
                    ProductColor(productId = it.id,
                        colorName = it.basicColorName,
                        image = it.image),
                    ProductColor(productId = it.id,
                        colorName = "pink",
                        image = R.drawable.image07),
                    ProductColor(productId = it.id,
                        colorName = "lemon",
                        image = R.drawable.image07),
                )
            },
        )
        private val adidasProducts = listOf(
            Product(
                id = 10,
                name = "Cozy 1-bedroom apartment with patio",
                image = R.drawable.image08,
                price = 14900.0,
                description = description,
                manufacturerId = 2,
                basicColorName = "green",
            ).also {
                it.colors = mutableListOf(
                    ProductColor(productId = it.id,
                        colorName = it.basicColorName,
                        image = it.image),
                    ProductColor(productId = it.id,
                        colorName = "red",
                        image = R.drawable.image08),
                )
            },

            Product(
                id = 12,
                name = "Furnished 1-bedroom apartment",
                image = R.drawable.image1,
                price = 15900.0,
                description = description,
                manufacturerId = 2,
                basicColorName = "gray",
            ).also {
                it.colors = mutableListOf(
                    ProductColor(productId = it.id,
                        colorName = it.basicColorName,
                        image = it.image),
                    ProductColor(productId = it.id,
                        colorName = "black",
                        image = R.drawable.image1),
                    ProductColor(productId = it.id,
                        colorName = "red",
                        image = R.drawable.image1),
                )
            },
        )
        private val paymentProviders = listOf(
            PaymentProvider(
                id = "apple",
                title = R.string.apple_pay,
                icon = R.drawable.ic_apple,
            ),
            PaymentProvider(
                id = "master",
                title = R.string.master_card,
                icon = R.drawable.ic_master_card,
            ),
            PaymentProvider(
                id = "visa",
                title = R.string.visa,
                icon = R.drawable.ic_visa,
            ),
        )
        private val userPaymentAccounts = listOf(
            UserPaymentProvider(
                providerId = "apple",
                cardNumber = "8402-5739-2039-5784"
            ),
            UserPaymentProvider(
                providerId = "master",
                cardNumber = "3323-8202-4748-2009"
            ),
            UserPaymentProvider(
                providerId = "visa",
                cardNumber = "7483-02836-4839-2833"
            ),
        )
        private val userLocation = Location(
            address = "AlTaif 51, st 5",
            city = "Khartoum",
            country = "Sudan",
        )

        init {
            nikeProducts.onEach {
                it.sizes = mutableListOf(
                    ProductSize(it.id, 38),

                )
            }
            adidasProducts.onEach {
                it.sizes = mutableListOf(
                    ProductSize(it.id, 38),

                )
            }

            scope.launch {
                populateDatabase(dao = client.get().getDao(), scope = scope)
            }
        }

        private suspend fun populateDatabase(dao: RoomDao, scope: CoroutineScope) {
            /** Save users */
            scope.launch {
                dao.saveUser(
                    User(
                        userId = 1,
                        name = "Merwan",
                        profile = R.drawable.ic_profile_empty,
                        phone = "+249922943879",
                        email = "mustfaibra@gmail.com",
                        password = "12344321",
                        token = "ds2f434ls2ks2lsj2ls",
                    )
                )
            }
            /** insert manufacturers */
            scope.launch {
                manufacturers.forEach {
                    dao.insertManufacturer(it)
                }
            }
            /** insert advertisements */
            scope.launch {
                advertisements.forEach {
                    dao.insertAdvertisement(it)
                }
            }
            /** Insert products */
            scope.launch {
                nikeProducts.plus(adidasProducts).forEach {
                    /** Insert the product itself */
                    dao.insertProduct(product = it)
                    /** Insert colors */
                    it.colors?.forEach { productColor ->
                        dao.insertOtherProductCopy(productColor)
                    }
                    /** Insert size */
                    it.sizes?.forEach { productSize ->
                        dao.insertSize(productSize)
                    }
                }
            }
            /** Insert payment providers */
            scope.launch {
                paymentProviders.forEach {
                    dao.savePaymentProvider(paymentProvider = it)
                }
            }
            /** Insert user's payment providers */
            scope.launch {
                userPaymentAccounts.forEach {
                    dao.saveUserPaymentProvider(it)
                }
            }
            /** Insert user's location */
            scope.launch {
                dao.saveLocation(location = userLocation)
            }
        }
    }

}