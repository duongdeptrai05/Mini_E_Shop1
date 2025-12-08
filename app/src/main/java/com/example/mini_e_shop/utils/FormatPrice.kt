package com.example.mini_e_shop.utils

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "US")).format(price)
}
