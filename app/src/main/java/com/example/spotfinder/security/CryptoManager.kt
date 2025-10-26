package com.example.spotfinder.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * Esta clase maneja toda la lógica de encriptación y desencriptación
 * usando el AndroidKeystore para máxima seguridad.
 */
class CryptoManager {

    // 1. Configuración de la bóveda (KeyStore)
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    // 2. Generador de la llave secreta
    private val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

    // 3. El "Cifrador" que usaremos (AES)
    private val cipher = Cipher.getInstance(TRANSFORMATION)

    // Nombre de nuestra llave secreta en la bóveda
    private val KEY_ALIAS = "spotfinder_secret_key"

    // Transformación estándar para encriptar/desencriptar
    companion object {
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }

    /**
     * Función principal para ENCRIPTAR.
     * Recibe los datos a encriptar (bytes) y un OutputStream donde escribirá los datos encriptados.
     */
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        // 1. Obtenemos la llave secreta (o la creamos si no existe)
        val secretKey = getOrCreateSecretKey(KEY_ALIAS)

        // 2. Inicializamos el Cifrador en modo ENCRIPTAR
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        // 3. Escribimos el "Vector de Inicialización" (IV) en el stream.
        // Esto es crucial para que la desencriptación funcione.
        val iv = cipher.iv
        outputStream.write(iv.size)
        outputStream.write(iv)

        // 4. Escribimos los datos encriptados en el stream
        val encryptedBytes = cipher.doFinal(bytes)
        outputStream.write(encryptedBytes.size)
        outputStream.write(encryptedBytes)

        return encryptedBytes
    }

    /**
     * Función principal para DESENCRIPTAR.
     * Recibe un InputStream de donde leerá los datos encriptados.
     */
    fun decrypt(inputStream: InputStream): ByteArray {
        // 1. Leemos el IV (Vector de Inicialización) que guardamos al encriptar
        val ivSize = inputStream.read()
        val iv = ByteArray(ivSize)
        inputStream.read(iv)

        // 2. Leemos los datos encriptados
        val encryptedDataSize = inputStream.read()
        val encryptedData = ByteArray(encryptedDataSize)
        inputStream.read(encryptedData)

        // 3. Obtenemos nuestra llave secreta de la bóveda
        val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

        // 4. Inicializamos el Cifrador en modo DESENCRIPTAR usando el IV
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        // 5. Desencriptamos y devolvemos los datos originales
        return cipher.doFinal(encryptedData)
    }

    /**
     * Ayudante: Busca la llave en la bóveda. Si no existe, la crea.
     */
    private fun getOrCreateSecretKey(alias: String): SecretKey {
        // Buscamos la llave
        val key = keyStore.getKey(alias, null)
        // Si la encontramos, la devolvemos
        if (key != null) {
            return key as SecretKey
        }

        // Si no, creamos una nueva
        val params = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true) // ¡¡IMPORTANTE!!
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        keyGenerator.init(params)
        return keyGenerator.generateKey()
    }
}