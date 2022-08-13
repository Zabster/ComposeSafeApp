@file:Suppress("DEPRECATION")

package com.zabster.safeappcompose.db.helper.secure

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricPrompt
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class AppSecureUtilsImpl(
    private val context: Context
) : AppSecureUtils {

    private val safeCipher = Cipher.getInstance(TRANSFORMATION)
    private val keyStore: KeyStore
    private val keyPair: KeyPair

    init {
        keyStore = loadKeyStore()
        keyPair = createKey()
    }

    override fun haveKeys(): Boolean = runCatching { keyStore.isKeyEntry(ALIAS) }
        .getOrDefault(false)

    override fun encrypt(password: String): String {
        safeCipher.init(Cipher.ENCRYPT_MODE, getPublicKey())
        val pasBytes = safeCipher.doFinal(password.toByteArray())
        return Base64.encodeToString(pasBytes, Base64.NO_WRAP)
    }

    override fun decrypt(password: String): String {
        safeCipher.init(Cipher.DECRYPT_MODE, getPrivateKey())
        val encryptedData = Base64.decode(password, Base64.NO_WRAP)
        return safeCipher.doFinal(encryptedData).toString(Charsets.UTF_8)
    }

    override fun createBiometricCryptoObject(): BiometricPrompt.CryptoObject {
        safeCipher.init(Cipher.DECRYPT_MODE, getPrivateKey())
        return BiometricPrompt.CryptoObject(safeCipher)
    }

    private fun loadKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(TYPE, PROVIDER) ?: throw IllegalArgumentException()
        keyStore.load(null)
        return keyStore
    }

    private fun getPublicKey() = keyPair.public
        ?: keyStore.getCertificate(ALIAS)?.publicKey
        ?: throw IllegalArgumentException()

    private fun getPrivateKey() = keyPair.private
        ?: keyStore.getKey(ALIAS, null)
        ?: throw IllegalArgumentException()

    private fun createKey(): KeyPair {
        val keyGen =
            KeyPairGenerator.getInstance(ALGORITHM, PROVIDER) ?: throw IllegalArgumentException()
        val specBuilder = getSpec()
        keyGen.initialize(specBuilder)
        return if (haveKeys()) {
            KeyPair(
                keyStore.getCertificate(ALIAS)?.publicKey,
                keyStore.getKey(ALIAS, null) as? PrivateKey
            )
        } else {
            keyGen.generateKeyPair()
        }
    }

    private fun getSpec() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, 100)
        }
        KeyPairGeneratorSpec.Builder(context)
            .setAlias(ALIAS)
            .setSubject(X500Principal("CN=$ALIAS"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(startDate.time)
            .setEndDate(endDate.time)
            .build()
    } else {
        val purpose = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        KeyGenParameterSpec.Builder(ALIAS, purpose)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
    }

    companion object {
        private const val ALIAS = "SafePassAppAlias"
        private const val ALGORITHM = "RSA"
        private const val PROVIDER = "AndroidKeyStore"
        private const val TYPE = "AndroidKeyStore"
        private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    }
}