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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.Chronometer.*;


// Class to store the score and user name pairs
class Score implements Comparable<Score> {
    int score;
    String name;
    String date;

    public Score(int score, String name, String date) {
        this.score = score;
        this.name = name;
        this.date = date;
    }

    @Override
    public int compareTo(Score o) {
        return score < o.score ? -1 : score > o.score ? 1 : 0;
    }
}


// Main Class
public class Game extends Activity {
	
	String notes[]={"don_1","re_1","mi_1","fa_1","sol_1","la_1","si_1","don_2","re_2","mi_2","fa_2","sol_2","la_2","si_2"};
	int notesbt[]={R.id.don,R.id.re,R.id.mi,R.id.fa,R.id.sol,R.id.la,R.id.si};
	int prevnotenum;
	int correct, fail;
	int scoresnum = 20;   // scores showed in hall of fame
	boolean omt;
	long elapsedtime;
	int countdown;
	Chronometer chrono;
	String currenttime;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game); 
        omt = this.getIntent().getExtras().getBoolean("omt");
        if (savedInstanceState != null){                              // game activity has been restarted by a runtime change (orientation change)
        	correct = savedInstanceState.getInt("correct");
        	fail = savedInstanceState.getInt("fail");
        	elapsedtime = savedInstanceState.getLong("elapsedtime");
        	countdown = savedInstanceState.getInt("countdown");
        }
        else {
        	correct = 0;
        	fail = 0;
        	if (omt==true) {countdown = 61;}
        	else {elapsedtime = SystemClock.elapsedRealtime();}
        }
        clickListenersInitialization();
        chrono = (Chronometer) findViewById(R.id.chrono);
        chrono.setOnChronometerTickListener(ChronometerTickListener);
        chrono.setBase(elapsedtime); 
        chrono.start();
        showNextNote();
    }
    
    
    // Set the OnClick Listeners for all the buttons
    private void clickListenersInitialization() {
        Button bt;
        int x;
        
        for (x=0;x<notesbt.length;x++) {
        	bt = (Button) findViewById(notesbt[x]);
        	bt.setOnClickListener(ClickListener);
        }
        	
        bt = (Button) findViewById(R.id.endgame);
        bt.setOnClickListener(ClickListener); 
    }
    
    
    // Set a red pressed color button for all note buttons except notebt (green color = right note button)
    private void setRedBackgroundToAllBtExceptThis(String notebt) {
        Button bt;
        String note;
        int x;
        
        for (x=0;x<notesbt.length;x++) {
        
        	bt = (Button) findViewById(notesbt[x]);
       
        	note = String.valueOf(bt.getTag());
        	if (note.equals(notebt)) { 
        		bt.setBackgroundResource(R.drawable.custom_button_green); 
        	}
        	else { 
        		bt.setBackgroundResource(R.drawable.custom_button_red); 
        	}
        }
    }
    
    
    // Randomly gets and show the next note
    private void showNextNote() {
    	Random randomGen = new Random();
    	int notenum = randomGen.nextInt(12);
    	while (notenum == prevnotenum) { notenum = randomGen.nextInt(12);}
    	prevnotenum = notenum;
    	String note = notes[notenum];
        ImageView scoreimg = (ImageView) findViewById(R.id.imgnota);
        int resid = getResources().getIdentifier(note, "drawable", this.getPackageName());
        scoreimg.setImageResource(resid);
        String notebt = note.substring(0, note.length()-2);
        setRedBackgroundToAllBtExceptThis(notebt);
    }
    
    
    // Returns the file content in a string
    public String getStringFromFile(String file){
    	FileInputStream fis;
        int ch;
        StringBuffer strContent = new StringBuffer("");
		try {
			fis = openFileInput(file);
			 while((ch = fis.read()) != -1)
			        strContent.append((char)ch);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strContent.toString();
    }
      
  
    // Is this score in the top scores?
    public Boolean isInTheTopScores(Integer userscore) {
      
		List<Score> scores = new ArrayList<Score>();
        
        String filecontent = getStringFromFile("halloffame");              // This file is formated in this way: user1,score1;user2,score2;user3,score3; ...
        
        if (filecontent != "") {
	        String scoresarray[] = filecontent.split(";");                 // Split the string in "user,score" strings into an array
	        
	        for (int i = 0; i < scoresarray.length; i++){  	               // Loop Through the scorearray, split user and score pairs and add them to the list
	        	String scorearray[];
	        	scorearray = scoresarray[i].split(",");  
	        	scores.add(new Score(Integer.parseInt(scorearray[0]), scorearray[1], scorearray[2]));
	        }
        }
  
        Collections.sort(scores);
        ListIterator<Score> i = scores.listIterator(); 
        
        if (i.hasNext() && scores.size()>=scoresnum) {
        	Score worstscore=(Score)i.next();
        	return (userscore >= worstscore.score) ? true : false;
        }
        else return true;    
    }
    
    
    // Save the score list to halloffame file
    private void saveListInFile(List<Score> scores){
    	
    	ListIterator<Score> i = scores.listIterator(scores.size());
      
        FileOutputStream fos = null;
        
        try {
        	
 			fos = openFileOutput("halloffame", Context.MODE_PRIVATE);
 			
 		} catch (FileNotFoundException e1) {
 			e1.printStackTrace();
 		}
         
 		int entries = 1;
        while(i.hasPrevious() && entries<=scoresnum)
        {	
        	Score entry=(Score)i.previous();

         	String name = String.valueOf(entry.score) + "," + entry.name + "," + entry.date + ";";
 			
 			try {
 				
 				fos.write(name.getBytes());
 				
 			} catch (FileNotFoundException e) {
 				e.printStackTrace();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 			entries++;			
        }
        try {
        	
        	fos.close();
        	
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
    }
    
    
    // Save the new user score into the file maintaining the score order (inverse, top scores up)
    private void saveUserScore(Score userscore) {
      
		List<Score> scores = new ArrayList<Score>();
        
        String filecontent = getStringFromFile("halloffame");      // This file is formated in this way: user1,score1;user2,score2;user3,score3; ...
        
        if (filecontent != "") {
	        String scoresarray[] = filecontent.split(";");             // Split the string in "user,score" strings into an array
	        
	        for (int i=0; i<scoresarray.length; i++){  	               // Loop Through the scorearray, split user and score pairs and add them to the list
	        	String scorearray[];
	        	scorearray = scoresarray[i].split(",");  
	        	scores.add(new Score(Integer.parseInt(scorearray[0]), scorearray[1], scorearray[2]));
	        }
        }
        scores.add(userscore);                                     // Add the new score into the list
        
        Collections.sort(scores);
        
        saveListInFile(scores);    
    }
    
    
    // Show the end dialog, save the score (only if is in topX) and return to game menu 
    private void showOMTEndDialog() {
    	final AlertDialog.Builder playername = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);

    	int points = correct-fail;
    	
    	if (isInTheTopScores(points)){
   	
	    	playername.setMessage( correct + " correct notes and " + fail + " fails in one minute test. \n \n Congratulations you are in top "+scoresnum+" scores! \n \n Please, insert your name:"); 
	    	playername.setIcon(R.drawable.dialog);
	    	playername.setTitle("Results");
	    	playername.setView(input);
	    	playername.setPositiveButton("Save score", new DialogInterface.OnClickListener() {
	    		
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			
	    			String name = input.getText().toString().trim();
	    			if (name.length()>12)  name = name.substring(0, 12);                                // 12 chars max.
	    	        name = name.replaceAll(",", "");                             // avoiding presence of "," and ";" cause is the char used to separate entries in the file (name1,score1;name2,score2;...)
	    	        name = name.replaceAll(";", "");
	    			int points = correct-fail;
	    			Calendar cal = Calendar.getInstance();
	    		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    		    String date = sdf.format(cal.getTime());
	    			
	    			saveUserScore(new Score(points, name, date));
	    			
		        	Intent intent = new Intent();
		        	intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Menu");
		        	startActivity(intent);
	    		}
	    	});
	    	
	    	playername.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			Intent intent = new Intent();
	    			intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Menu");
	    			startActivity(intent);
	    		}
	        });
	    	
	    	playername.show();
    	}
    	else {
    		
    		playername.setIcon(R.drawable.dialog);
    		playername.setTitle("Results");
    		playername.setMessage( correct + " correct notes and " + fail + " fails in one minute test!"); 
    		playername.setCancelable(false);
    		playername.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   Intent intent = new Intent();
        	        	   intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Menu");
        	        	   startActivity(intent);
        	           }
        	       });
    		playername.show();	  				
    	}    
    }
    
    
    // Show the end dialog and return to game menu 
    private void showTrainingEndDialog() {
    	AlertDialog.Builder dlgbuilder = new AlertDialog.Builder(this);
    	dlgbuilder.setIcon(R.drawable.dialog);
    	dlgbuilder.setTitle("Results");
    	dlgbuilder.setMessage( correct + " correct notes and " + fail + " fails in " + currenttime + " minutes!");
    	dlgbuilder.setCancelable(false);
    	dlgbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   Intent intent = new Intent();
    	        	   intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Menu");
    	        	   startActivity(intent);
    	           }
    	       });
    	dlgbuilder.show();	
    }
    
    
    // Click listener for all the buttons
    OnClickListener ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
			case R.id.endgame:
				chrono.stop();
				if (omt==true) {
					Intent intent = new Intent();
					intent.setClassName("net.fercanet.LNM", "net.fercanet.LNM.Menu");
					startActivity(intent);
				}
				else showTrainingEndDialog();
			break;
			default:	
				Button bt = (Button) v;				
			    String bttext = String.valueOf(bt.getTag());
			    if ((notes[prevnotenum].equals(bttext+"_1")) || (notes[prevnotenum].equals(bttext+"_2"))){
					correct++;
					showNextNote();
				}
				else {
					fail++;
				}	
			}
		}
    };	

    // Chrono ticklistener to control the time and update the timer label
    OnChronometerTickListener ChronometerTickListener = new OnChronometerTickListener() {
    	@Override
		public void onChronometerTick(Chronometer chronometer) { 		
    		if (omt==true){
    			countdown--;
    			chronometer.setText(String.valueOf(countdown));
    			if (countdown<=0) {
    				chrono.stop();
    				showOMTEndDialog();
    			}
    		}
    		else {
    			long minutes=((SystemClock.elapsedRealtime()-chrono.getBase())/1000)/60;
	    		long seconds=((SystemClock.elapsedRealtime()-chrono.getBase())/1000)%60;
	    		currenttime=minutes+":"+seconds;
	    		chronometer.setText(currenttime);
	    		elapsedtime=SystemClock.elapsedRealtime();
    		}
		}
    };
    
    // If game activity is restarted by a runtime change (orientation change) this save some data to restore the game 
    protected void onSaveInstanceState(Bundle outState)
    {
      super.onSaveInstanceState(outState);
      outState.putInt("correct", correct);
      outState.putInt("fail", fail);
      outState.putLong("elapsedtime", chrono.getBase());
      outState.putInt("countdown", countdown);
    }

    
};


	

