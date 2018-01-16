package com.gregmcgowan.fivesorganiser.core.permissions

interface PermissionResults {

  fun onPermissionGranted()

  fun onPermissionDenied(userSaidNever: Boolean)
}

