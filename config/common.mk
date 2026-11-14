# Common WandererOS 1.0 Configurations
include vendor/wanderer/config/version.mk

# Strip AudioFX completely
TARGET_EXCLUDES_AUDIOFX := true

# Common Overlays
PRODUCT_PACKAGE_OVERLAYS += vendor/wanderer/overlay/common

# Include Wanderer Custom GApps Stack if present
$(call inherit-product-if-exists, vendor/gapps/gapps.mk)

# Base packages
PRODUCT_PACKAGES += \
    ThemePicker \
    WallpaperPicker2 \
    ExactCalculator \
    DeskClock \
    LatinIME

# Strip AudioFX & Lineage Music (Eleven/Twelve) from product packages
PRODUCT_PACKAGES := $(filter-out \
    AudioFX \
    Eleven \
    Twelve \
    Music, \
    $(PRODUCT_PACKAGES))

# Security, Privacy & Stealth Defaults
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.secure=1 \
    ro.debuggable=0 \
    ro.adb.secure=1 \
    persist.sys.pi_spoof=1 \
    persist.sys.keystore_spoof=1 \
    persist.sys.bank_stealth=1

# Common system properties
PRODUCT_SYSTEM_PROPERTIES += \
    ro.url.legal=http://www.google.com/intl/%s/mobile/android/basic/phone-legal.html \
    ro.error.receiver.system.apps=com.google.android.gms
