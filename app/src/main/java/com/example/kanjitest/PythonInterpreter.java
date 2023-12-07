package com.example.kanjitest;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.PyException;

import java.util.List;

public class PythonInterpreter {


    private PyObject libModule;

    public PythonInterpreter(){
        try{
            Python py = Python.getInstance();
            libModule = py.getModule("pythonGrader");
        } catch (PyException e){
            e.printStackTrace();
            //Handle initialization errors
        }
    }


    public String gradeUserKanji(List<List<Float>> strokes, String targetKanjiUnicode, boolean useFuzzy){
        try {
            if (libModule != null) {
                // Call the python function with user's strokes and target kanji's Unicode
                PyObject result = libModule.callAttr("grade_user_kanji", strokes, targetKanjiUnicode, useFuzzy);
                // Process and return the result as needed
                return result.toString();
            }
        } catch (PyException e) {
            e.printStackTrace();
            // Handle Python script errors
        }
        return null; // Return null or a default value in case of error
    }


    public String findClosestKanji(List<List<Float>> strokes, boolean fuzzy, boolean offby1){

        try {
            if(libModule != null){

/*
                Float[][] strokesArray =  new Float[strokes.size()][];
                for (int i = 0; i < strokes.size(); i++){
                    List<Float> stroke = strokes.get(i);
                    strokesArray[i] = stroke.toArray(new Float[0]);

                }*/
                //PyObject pyStrokes =  new PyObject(.getModule("builtins").callAttr("list", strokes));
                PyObject result = libModule.callAttr("find_closest_kanji", strokes, fuzzy, offby1);
                return result.toString();
            }

        }catch (PyException e){
            e.printStackTrace();
            //handle exception
        }
        return null;

    }
    }


