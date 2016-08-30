package com.sjy.listener;

import android.os.Bundle;

public interface IFragemDataListener {
	void OnDataChanged(Bundle bd);
	boolean doBackPress();
}
