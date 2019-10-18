package com.kms.appcore.networkstate;

import java.io.Serializable;

/**
 * @author ziv
 */

public enum NetState implements Serializable {

	CONNECTED, NOTCONNECTED, PING_SUCCESS, PING_FAILED;
}
