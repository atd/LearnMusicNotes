//   -----------------------------------------------------------------------------
//    Copyright 2010 Ferran Caellas Puig

//    This file is part of Learn Music Notes.
//
//    Learn Music Notes is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.

//    Learn Music Notes is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with Learn Music Notes.  If not, see <http://www.gnu.org/licenses/>.
//   -----------------------------------------------------------------------------


package net.fercanet.LNM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class Menu extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button mintest = (Button) findViewById(R.id.mintest);
        mintest.setOnClickListener(ClickListener);
        Button training = (Button) findViewById(R.id.training);
        training.setOnClickListener(ClickListener);
        Button hof = (Button) findViewById(R.id.hof);
        hof.setOnClickListener(ClickListener);
        Button endgame = (Button) findViewById(R.id.endgame);
        endgame.setOnClickListener(ClickListener);
    }
    
    // Click listener for the three menu buttons
    OnClickListener ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId())
			{
			case R.id.mintest:
		    	intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Game");
		    	intent.putExtra("omt", true);
		    	startActivity(intent);
			break;
			case R.id.training:	
		    	intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Game");
		    	intent.putExtra("omt", false);
		    	startActivity(intent);
			break;
			case R.id.hof:	
		    	intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Hof");
		    	startActivity(intent);

	    	break;
			case R.id.endgame:	
		    	moveTaskToBack(true);

	    	break;
			}
		}
    };	

}
