@file:Suppress("unused")

package com.kirillr.strictmodehelper.kotlin.dsl

import android.os.Build
import android.os.StrictMode
import android.os.strictmode.Violation
import java.util.concurrent.Executor
import kotlin.reflect.KClass

@VmPolicyDsl
class VmPolicyConfig private constructor(
    var activityLeaks: Boolean,
    var cleartextNetwork: Boolean,
    var contentUriWithoutPermission: Boolean,
    var fileUriExposure: Boolean,
    var leakedClosableObjects: Boolean,
    var leakedRegistrationObjects: Boolean,
    var leakedSqlLiteObjects: Boolean,
    var nonSdkApiUsage: Boolean,
    var untaggedSockets: Boolean,
    var implicitDirectBoot: Boolean,
    var credentialProtectedWhileLocked: Boolean,
    internal val penaltyConfig: PenaltyConfig
) {

    var classesInstanceLimit = mapOf<KClass<*>, Int>()

    fun penalty(config: (@VmPolicyDsl PenaltyConfig.() -> Unit)) {
        this.penaltyConfig.apply(config)
    }

    internal companion object {

        internal operator fun invoke(enableDefaults: Boolean): VmPolicyConfig {
            return if (enableDefaults) default() else disableAll()
        }

        private fun disableAll(): VmPolicyConfig {
            return VmPolicyConfig(
                activityLeaks = false,
                cleartextNetwork = false,
                contentUriWithoutPermission = false,
                fileUriExposure = false,
                leakedClosableObjects = false,
                leakedSqlLiteObjects = false,
                leakedRegistrationObjects = false,
                nonSdkApiUsage = false,
                untaggedSockets = false,
                implicitDirectBoot = false,
                credentialProtectedWhileLocked = false,
                penaltyConfig = PenaltyConfig(false)
            )
        }

        private fun default(): VmPolicyConfig {
            return VmPolicyConfig(
                activityLeaks = DEFAULT_ACTIVITY_LEAKS,
                cleartextNetwork = DEFAULT_CLEARTEXT_NETWORK,
                contentUriWithoutPermission = DEFAULT_CONTENT_URI_WITHOUT_PERMISSION,
                fileUriExposure = DEFAULT_FILE_URI_EXPOSURE,
                leakedClosableObjects = DEFAULT_LEAKED_CLOSABLE_OBJECTS,
                leakedSqlLiteObjects = DEFAULT_LEAKED_SQL_LITE_OBJECTS,
                leakedRegistrationObjects = DEFAULT_LEAKED_REGISTRATION_OBJECTS,
                nonSdkApiUsage = DEFAULT_NON_SDK_API_USAGE,
                untaggedSockets = DEFAULT_UNTAGGED_SOCKETS,
                implicitDirectBoot = DEFAULT_IMPLICIT_DIRECT_BOOT,
                credentialProtectedWhileLocked = DEFAULT_CREDENTIAL_PROTECTED_WHILE_LOCKED,
                penaltyConfig = PenaltyConfig(true)
            )
        }

        private const val DEFAULT_ACTIVITY_LEAKS = true
        private const val DEFAULT_CLEARTEXT_NETWORK = true
        private const val DEFAULT_CONTENT_URI_WITHOUT_PERMISSION = true
        private const val DEFAULT_FILE_URI_EXPOSURE = true
        private const val DEFAULT_LEAKED_CLOSABLE_OBJECTS = true
        private const val DEFAULT_LEAKED_REGISTRATION_OBJECTS = true
        private const val DEFAULT_LEAKED_SQL_LITE_OBJECTS = true
        private const val DEFAULT_NON_SDK_API_USAGE = true
        private const val DEFAULT_UNTAGGED_SOCKETS = true
        private const val DEFAULT_IMPLICIT_DIRECT_BOOT = true
        private const val DEFAULT_CREDENTIAL_PROTECTED_WHILE_LOCKED = true
    }

    class PenaltyConfig private constructor(
        var death: Boolean,
        var deathOnCleartextNetwork: Boolean,
        var deathOnFileUriExposure: Boolean,
        var dropBox: Boolean,
        var log: Boolean
    ) {

        internal var onViolation: ((violation: Violation) -> Unit)? = null
        internal var onViolationExecutor: Executor? = null

        /**
         * Call [StrictMode.OnThreadViolationListener.onThreadViolation] on specified [executor] every violation.
         *
         * Work on [Build.VERSION_CODES.P] and newer.
         */
        fun onViolation(executor: Executor, body: (violation: Violation) -> Unit) {
            onViolationExecutor = executor
            this.onViolation = body
        }

        internal companion object {

            internal operator fun invoke(enableDefaults: Boolean): PenaltyConfig {
                return if (enableDefaults) default() else disableAll()
            }

            private fun disableAll(): PenaltyConfig {
                return PenaltyConfig(
                    death = false,
                    deathOnCleartextNetwork = false,
                    deathOnFileUriExposure = false,
                    dropBox = false,
                    log = false
                )
            }

            private fun default(): PenaltyConfig {
                return PenaltyConfig(
                    death = DEFAULT_DEATH,
                    deathOnCleartextNetwork = DEFAULT_DEATH_ON_CLEARTEXT_NETWORK,
                    deathOnFileUriExposure = DEFAULT_DEATH_ON_FILE_URI_EXPOSURE,
                    dropBox = DEFAULT_DROPBOX,
                    log = DEFAULT_LOG
                )
            }

            private const val DEFAULT_DEATH = false
            private const val DEFAULT_DEATH_ON_CLEARTEXT_NETWORK = false
            private const val DEFAULT_DEATH_ON_FILE_URI_EXPOSURE = false
            private const val DEFAULT_DROPBOX = false
            private const val DEFAULT_LOG = true
        }
    }
}
