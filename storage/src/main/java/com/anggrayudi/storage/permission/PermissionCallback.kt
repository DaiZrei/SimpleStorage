package com.anggrayudi.storage.permission

/**
 * Created on 12/13/20
 * @author Anggrayudi H
 */
interface PermissionCallback {

    @JvmDefault
    fun onDisplayConsentDialog(request: PermissionRequest) {
        request.continueToPermissionRequest()
    }

    /**
     * @param fromSystemDialog true if users agreed/denied the permission from the system dialog.
     */
    fun onPermissionsChecked(result: PermissionResult, fromSystemDialog: Boolean)

    @JvmDefault
    fun onShouldRedirectToSystemSettings(blockedPermissions: List<PermissionReport>) {
        // default implementation
    }
}