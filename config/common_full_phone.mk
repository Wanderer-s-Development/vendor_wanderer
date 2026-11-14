# Inherit full base telephony configuration
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit common WandererOS configuration
$(call inherit-product, vendor/wanderer/config/common.mk)
