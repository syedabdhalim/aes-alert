package com.syed.aesalert.data

/**
 * AES/AWAS Speed Camera Data for Malaysia
 *
 * 29 speed enforcement cameras across Malaysian highways.
 * GPS coordinates sourced from OpenStreetMap + official JPJ data.
 * Bearing angles indicate the direction the camera monitors (traffic flow direction).
 *
 * Sources:
 * - JPJ Official AES/AWAS Camera List (2024-2025)
 * - OpenStreetMap Overpass API (highway=speed_camera nodes)
 * - Motorist Malaysia, MySumber, Carlist
 *
 * Last updated: 2026-02-20
 */
object CameraData {

    fun getAllCameras(): List<AESCamera> = listOf(

        // =====================================================================
        // PLUS Highway E2 - Southern Route (JB to KL)
        // KM 0 starts at Pandan Interchange, Johor Bahru
        // =====================================================================

        // 1. KM 1 PLUS - Johor Bahru (Northbound)
        AESCamera(
            id = 1,
            name = "KM1 PLUS JB (Utara)",
            latitude = 1.5276344,
            longitude = 103.7538075,
            speedLimit = 110,
            bearingAngle = 305f,
            direction = "Utara",
            state = "Johor",
            highway = "PLUS E2"
        ),

        // 2. KM 1 PLUS - Johor Bahru (Southbound)
        AESCamera(
            id = 2,
            name = "KM1 PLUS JB (Selatan)",
            latitude = 1.5276324,
            longitude = 103.7535588,
            speedLimit = 110,
            bearingAngle = 115f,
            direction = "Selatan",
            state = "Johor",
            highway = "PLUS E2"
        ),

        // 3. KM 146.8 PLUS - Pagoh (Southbound)
        AESCamera(
            id = 3,
            name = "KM146.8 PLUS Pagoh (Selatan)",
            latitude = 2.1497965,
            longitude = 102.6980349,
            speedLimit = 110,
            bearingAngle = 115f,
            direction = "Selatan",
            state = "Johor",
            highway = "PLUS E2"
        ),

        // 4. KM 151.4 PLUS - Pagoh (Northbound)
        AESCamera(
            id = 4,
            name = "KM151.4 PLUS Pagoh (Utara)",
            latitude = 2.1582863,
            longitude = 102.6590445,
            speedLimit = 110,
            bearingAngle = 305f,
            direction = "Utara",
            state = "Johor",
            highway = "PLUS E2"
        ),

        // 5. KM 184.2 PLUS - Jasin (Northbound)
        AESCamera(
            id = 5,
            name = "KM184.2 PLUS Jasin (Utara)",
            latitude = 2.2841020,
            longitude = 102.4088355,
            speedLimit = 110,
            bearingAngle = 90f,
            direction = "Utara",
            state = "Melaka",
            highway = "PLUS E2"
        ),

        // 6. KM 185 PLUS - Bemban (Southbound)
        AESCamera(
            id = 6,
            name = "KM185 PLUS Bemban (Selatan)",
            latitude = 2.2845148,
            longitude = 102.4019710,
            speedLimit = 110,
            bearingAngle = 270f,
            direction = "Selatan",
            state = "Melaka",
            highway = "PLUS E2"
        ),

        // 7. KM 214.4 PLUS - Alor Gajah (Northbound)
        AESCamera(
            id = 7,
            name = "KM214.4 PLUS Alor Gajah (Utara)",
            latitude = 2.4333218,
            longitude = 102.2049056,
            speedLimit = 110,
            bearingAngle = 140f,
            direction = "Utara",
            state = "Melaka",
            highway = "PLUS E2"
        ),

        // 8. KM 214.4 PLUS - Alor Gajah (Southbound)
        AESCamera(
            id = 8,
            name = "KM214.4 PLUS Alor Gajah (Selatan)",
            latitude = 2.4330167,
            longitude = 102.2053318,
            speedLimit = 110,
            bearingAngle = 320f,
            direction = "Selatan",
            state = "Melaka",
            highway = "PLUS E2"
        ),

        // 9. KM 301.6 PLUS - Kajang (Northbound)
        AESCamera(
            id = 9,
            name = "KM301.6 PLUS Kajang (Utara)",
            latitude = 2.9743424,
            longitude = 101.7426033,
            speedLimit = 90,
            bearingAngle = 350f,
            direction = "Utara",
            state = "Selangor",
            highway = "PLUS E2"
        ),

        // =====================================================================
        // PLUS Highway E1 - Northern Route (KL to Bukit Kayu Hitam)
        // KM 0 starts at Bukit Lanjan
        // =====================================================================

        // 10. KM 204.6 PLUS - Taiping (Northbound)
        AESCamera(
            id = 10,
            name = "KM204.6 PLUS Taiping (Utara)",
            latitude = 4.9053251,
            longitude = 100.6681468,
            speedLimit = 110,
            bearingAngle = 12f,
            direction = "Utara",
            state = "Perak",
            highway = "PLUS E1"
        ),

        // 11. KM 299.9 PLUS - Kampar/Gua Tempurung (Northbound)
        AESCamera(
            id = 11,
            name = "KM299.9 PLUS Kampar (Utara)",
            latitude = 4.4346215,
            longitude = 101.1894700,
            speedLimit = 110,
            bearingAngle = 310f,
            direction = "Utara",
            state = "Perak",
            highway = "PLUS E1"
        ),

        // 12. KM 375.9 PLUS - Slim River (Northbound)
        AESCamera(
            id = 12,
            name = "KM375.9 PLUS Slim River (Utara)",
            latitude = 3.8360627,
            longitude = 101.4217219,
            speedLimit = 110,
            bearingAngle = 325f,
            direction = "Utara",
            state = "Perak",
            highway = "PLUS E1"
        ),

        // 13. KM 382.8 PLUS - Behrang (Southbound)
        AESCamera(
            id = 13,
            name = "KM382.8 PLUS Behrang (Selatan)",
            latitude = 3.7823666,
            longitude = 101.4466177,
            speedLimit = 110,
            bearingAngle = 180f,
            direction = "Selatan",
            state = "Perak",
            highway = "PLUS E1"
        ),

        // 14. KM 166 PLUS - Seberang Perai Selatan / Nibong Tebal (Southbound)
        AESCamera(
            id = 14,
            name = "KM166 PLUS Jawi (Selatan)",
            latitude = 5.1646970,
            longitude = 100.5071246,
            speedLimit = 110,
            bearingAngle = 155f,
            direction = "Selatan",
            state = "Pulau Pinang",
            highway = "PLUS E1"
        ),

        // 15. KM 97.2 PLUS - Kuala Muda (Northbound)
        AESCamera(
            id = 15,
            name = "KM97.2 PLUS Kuala Muda (Utara)",
            latitude = 5.6913635,
            longitude = 100.5206907,
            speedLimit = 110,
            bearingAngle = 15f,
            direction = "Utara",
            state = "Kedah",
            highway = "PLUS E1"
        ),

        // 16. KM 174 PLUS - Bandar Baharu (Northbound)
        AESCamera(
            id = 16,
            name = "KM174 PLUS Bandar Baharu (Utara)",
            latitude = 5.1135420,
            longitude = 100.5460392,
            speedLimit = 110,
            bearingAngle = 350f,
            direction = "Utara",
            state = "Kedah",
            highway = "PLUS E1"
        ),

        // =====================================================================
        // ELITE Highway E6 - North-South Expressway Central Link
        // KM 0 starts at Shah Alam Interchange
        // =====================================================================

        // 17. KM 17 ELITE - towards KLIA (Southbound)
        // Note: OSM has this as "AWAS ELITE - Northbound" at bearing 330 and
        //       "AWAS ELITE Highway - Southbound" at bearing 140.
        //       KM17 is the southbound camera, KM28.4 is the northbound camera.
        AESCamera(
            id = 17,
            name = "KM17 ELITE (Selatan)",
            latitude = 2.9554883,
            longitude = 101.5851907,
            speedLimit = 110,
            bearingAngle = 140f,
            direction = "Selatan",
            state = "Selangor",
            highway = "ELITE E6"
        ),

        // 18. KM 28.4 ELITE - towards Shah Alam (Northbound)
        AESCamera(
            id = 18,
            name = "KM28.4 ELITE (Utara)",
            latitude = 2.8680186,
            longitude = 101.6386222,
            speedLimit = 110,
            bearingAngle = 330f,
            direction = "Utara",
            state = "Selangor",
            highway = "ELITE E6"
        ),

        // =====================================================================
        // Guthrie Corridor Expressway E35
        // =====================================================================

        // 19. KM 18 GCE - Southbound
        AESCamera(
            id = 19,
            name = "KM18 GCE (Selatan)",
            latitude = 3.2310311,
            longitude = 101.5195615,
            speedLimit = 110,
            bearingAngle = 245f,
            direction = "Selatan",
            state = "Selangor",
            highway = "GCE E35"
        ),

        // 20. KM 18 GCE - Northbound
        AESCamera(
            id = 20,
            name = "KM18 GCE (Utara)",
            latitude = 3.2311659,
            longitude = 101.5194786,
            speedLimit = 110,
            bearingAngle = 60f,
            direction = "Utara",
            state = "Selangor",
            highway = "GCE E35"
        ),

        // =====================================================================
        // SKVE - South Klang Valley Expressway E26
        // =====================================================================

        // 21. KM 6.6 SKVE
        AESCamera(
            id = 21,
            name = "KM6.6 SKVE",
            latitude = 2.9734922,
            longitude = 101.6772281,
            speedLimit = 80,
            bearingAngle = 100f,
            direction = "Timur",
            state = "Selangor",
            highway = "SKVE E26"
        ),

        // =====================================================================
        // LEKAS - Kajang-Seremban Highway E21
        // =====================================================================

        // 22. KM 21 LEKAS - Southbound (towards Seremban)
        AESCamera(
            id = 22,
            name = "KM21 LEKAS Mantin (Selatan)",
            latitude = 2.8198785,
            longitude = 101.8690288,
            speedLimit = 110,
            bearingAngle = 125f,
            direction = "Selatan",
            state = "Negeri Sembilan",
            highway = "LEKAS E21"
        ),

        // 23. KM 21 LEKAS - Northbound (towards Kajang)
        AESCamera(
            id = 23,
            name = "KM21 LEKAS Mantin (Utara)",
            latitude = 2.8199167,
            longitude = 101.8686983,
            speedLimit = 110,
            bearingAngle = 310f,
            direction = "Utara",
            state = "Negeri Sembilan",
            highway = "LEKAS E21"
        ),

        // =====================================================================
        // Jalan Lebuh Sentosa - Putrajaya
        // =====================================================================

        // 24. KM 1.6 Lebuh Sentosa, Putrajaya
        AESCamera(
            id = 24,
            name = "KM1.6 Lebuh Sentosa Putrajaya",
            latitude = 2.9455561,
            longitude = 101.6838053,
            speedLimit = 70,
            bearingAngle = 180f,
            direction = "Selatan",
            state = "WP Putrajaya",
            highway = "Lebuh Sentosa"
        ),

        // =====================================================================
        // Jalan Ipoh-Kuala Lumpur (Federal Route 1)
        // =====================================================================

        // 25. KM 85.5 Jalan Ipoh-KL, Sungkai (Southbound)
        AESCamera(
            id = 25,
            name = "KM85.5 Jalan Ipoh-KL Sungkai (Selatan)",
            latitude = 3.9621952,
            longitude = 101.3287215,
            speedLimit = 90,
            bearingAngle = 200f,
            direction = "Selatan",
            state = "Perak",
            highway = "Jalan Persekutuan 1"
        ),

        // =====================================================================
        // Gua Musang - Kuala Krai (Federal Route 8)
        // =====================================================================

        // 26. KM 17 Gua Musang - Kuala Krai
        AESCamera(
            id = 26,
            name = "KM17 Gua Musang-Kuala Krai",
            latitude = 4.9587156,
            longitude = 102.0748614,
            speedLimit = 90,
            bearingAngle = 80f,
            direction = "Timur",
            state = "Kelantan",
            highway = "Jalan Persekutuan 8"
        ),

        // =====================================================================
        // LPT2 - East Coast Expressway Phase 2 (E8)
        // Jabor to Kuala Terengganu
        // =====================================================================

        // 27. KM 256.1 LPT2 - towards KL (Westbound/Southbound)
        // Near Perasing RSA area
        AESCamera(
            id = 27,
            name = "KM256.1 LPT2 Perasing (Arah KL)",
            latitude = 3.9888159,
            longitude = 103.3026472,
            speedLimit = 110,
            bearingAngle = 220f,
            direction = "Barat",
            state = "Terengganu",
            highway = "LPT2 E8"
        ),

        // 28. KM 288.6 LPT2 - towards Kuala Terengganu (Northbound/Eastbound)
        // Near Ajil area
        AESCamera(
            id = 28,
            name = "KM288.6 LPT2 Ajil (Arah KT)",
            latitude = 5.0540000,
            longitude = 103.1120000,
            speedLimit = 110,
            bearingAngle = 40f,
            direction = "Timur",
            state = "Terengganu",
            highway = "LPT2 E8"
        ),

        // =====================================================================
        // Bukit Beruntung area - PLUS E1 (additional camera from OSM)
        // =====================================================================

        // 29. Bukit Beruntung - Southbound
        AESCamera(
            id = 29,
            name = "PLUS Bukit Beruntung (Selatan)",
            latitude = 3.3980726,
            longitude = 101.5486198,
            speedLimit = 110,
            bearingAngle = 325f,
            direction = "Selatan",
            state = "Selangor",
            highway = "PLUS E1"
        )
    )
}
