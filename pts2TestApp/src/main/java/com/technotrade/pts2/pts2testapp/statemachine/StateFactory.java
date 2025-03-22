package com.technotrade.pts2.pts2testapp.statemachine;

import com.technotrade.pts2.pts2testapp.statemachine.states.IdleState;
import com.technotrade.pts2.pts2testapp.statemachine.states.NozzleSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.PumpSelectedState;

/// <summary>
/// State factory class
/// </summary>
public class StateFactory {
    public static BaseState createState(String stateName) {
        switch (stateName) {
            case IdleState.NAME:
                return new IdleState();
            case PumpSelectedState.NAME:
                return new PumpSelectedState();
            case NozzleSelectedState.NAME:
                return new NozzleSelectedState();
            default:
                return null;
        }
    }
}