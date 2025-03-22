package com.technotrade.pts2.pts2testapp.functional;

import com.technotrade.pts2.pts2testapp.entity.PumpItem;

@FunctionalInterface
public interface PumpAction {
	void execute(PumpItem item);
}