package com.example.socialapp.config

import com.cloudinary.Cloudinary

object CloudinaryConfig {
    val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dqtakgruf",
            "api_key" to "597257989842113",
            "api_secret" to "KLAdjJ490nkIYZTEdTDMNXyPIuQ"
        )
    )
}
