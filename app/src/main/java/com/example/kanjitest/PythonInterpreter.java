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
            libModule = py.getModule("lib");
        } catch (PyException e){
            e.printStackTrace();
            //Handle initialization errors
        }
    }

    public String analyzeDrawing(List<List<Float>> strokes){
        try{
            if(libModule != null){
                // Call the python function
                PyObject result = libModule.callAttr("matches", strokes);
                // Assuming the function returns a list of tuples (score, kanji)
                List<PyObject> resultsList = result.asList();
                String output = "";
                for (PyObject tuple : resultsList) {
                    List<PyObject> tupleList = tuple.asList();
                    output += tupleList.get(1).toString() + " (" + tupleList.get(0).toString() + ")/n";
                }
                return output;
            }
        }catch (PyException e){
            e.printStackTrace();
            //Handle Python script errors
        }
        return null; //Return null or a default value in case of error
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
    }


