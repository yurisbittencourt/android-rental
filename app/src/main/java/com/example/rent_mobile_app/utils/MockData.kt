//package com.example.rent_mobile_app.utils
//
//import androidx.appcompat.app.AppCompatActivity
//import com.example.rent_mobile_app.models.property.Property
//import com.example.rent_mobile_app.models.property.PropertyTypeEnum
//import com.example.rent_mobile_app.models.user.User
//import com.example.rent_mobile_app.models.user.UserTypeEnum
//
//class MockData : AppCompatActivity() {
//
//    fun setMockProperty(storageActions: StorageActions) {
//        val property1 =
//            Property(
//                "Toronto Apartment",
//                PropertyTypeEnum.APARTMENT,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                3,
//                4,
//                1,
//                5,
//                1,
//                1,
//                1,
//                500.00,
//                3500.00,
//                "good condition, in front the lake blue-fish",
//                "40 Queen St",
//                "Toronto",
//                "MMM111",
//                true,
//                "",
//            )
//        val property2 =
//            Property(
//                "Toronto Basement",
//                PropertyTypeEnum.BASEMENT,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                400.00,
//                2000.00,
//                "Great basement rental, chill housemates",
//                "10 Sherborne St",
//                "Toronto",
//                "MMM222",
//                true,
//                "",
//            )
//        val property3 =
//            Property(
//                "Toronto Condo",
//                PropertyTypeEnum.CONDO,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                1,
//                1,
//                1,
//                2,
//                1,
//                1,
//                1,
//                600.00,
//                2600.00,
//                "Modern layout condo with great view of downtown",
//                "150 King St",
//                "Toronto",
//                "MMM333",
//                true,
//                "",
//            )
//        val property4 =
//            Property(
//                "Toronto House",
//                PropertyTypeEnum.HOUSE,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                3,
//                3,
//                1,
//                4,
//                1,
//                1,
//                1,
//                800.00,
//                4500.00,
//                "Luxurious house for rent in residential neighborhood",
//                "200 Elm St",
//                "Toronto",
//                "MMM444",
//                true,
//                "",
//            )
//        val property5 =
//            Property(
//                "Montreal Townhouse",
//                PropertyTypeEnum.TOWNHOUSE,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                3,
//                3,
//                1,
//                4,
//                1,
//                1,
//                1,
//                800.00,
//                4500.00,
//                "Luxurious house for rent in residential neighborhood",
//                "200 Elm St",
//                "Montreal",
//                "MMM555",
//                true,
//                "",
//            )
//        val property6 =
//            Property(
//                "Montreal Condo",
//                PropertyTypeEnum.CONDO,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                1,
//                1,
//                1,
//                2,
//                1,
//                1,
//                1,
//                600.00,
//                2600.00,
//                "Modern layout condo with great view of downtown",
//                "150 King St",
//                "Montreal",
//                "MMM666",
//                true,
//                "",
//            )
//        val property7 =
//            Property(
//                "Vancouver House",
//                PropertyTypeEnum.HOUSE,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                3,
//                3,
//                1,
//                4,
//                1,
//                1,
//                1,
//                800.00,
//                4500.00,
//                "Luxurious house for rent in residential neighborhood",
//                "200 Elm St",
//                "Vancouver",
//                "MMM555",
//                true,
//                "",
//            )
//        val property8 =
//            Property(
//                "Vancouver Condo",
//                PropertyTypeEnum.CONDO,
//                User(
//                    "Joe Silver",
//                    "joesilver@email.com",
//                    "123",
//                    "+1475825258",
//                    UserTypeEnum.LANDLORD.name,
//                    mutableListOf(),
//                    mutableListOf()
//                ),
//                1,
//                1,
//                1,
//                2,
//                1,
//                1,
//                1,
//                600.00,
//                2600.00,
//                "Modern layout condo with great view of downtown",
//                "150 King St",
//                "Vancouver",
//                "MMM888",
//                true,
//                "",
//            )
//
//        if (storageActions.getProperties().size == 0) {
//
//            storageActions.saveProperties(
//                mutableListOf(
//                    property1,
//                    property2,
//                    property3,
//                    property4,
//                    property5,
//                    property6,
//                    property7,
//                    property8,
//                )
//            )
//        }
//    }
//
//    fun setMockUser(storageActions: StorageActions) {
//        val propertyList = storageActions.getProperties()
//
//        val tenantUser = User(
//            "Mary Johnson",
//            "maryjohnson@email.com",
//            "123",
//            "+2655893652",
//            UserTypeEnum.TENANT.name,
//            mutableListOf(),
//            mutableListOf()
//        )
//        val landlordUser = User(
//            "Joe Silver",
//            "joesilver@email.com",
//            "123",
//            "+1475825258",
//            UserTypeEnum.LANDLORD.name,
//            mutableListOf(),
//            mutableListOf()
//        )
//
//        storageActions.saveUsers(mutableListOf(tenantUser, landlordUser))
//    }
//}