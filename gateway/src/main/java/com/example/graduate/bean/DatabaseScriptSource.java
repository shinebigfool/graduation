package com.example.graduate.bean;

import com.example.graduate.cache.GroovyInnerCache;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class DatabaseScriptSource implements ScriptSource {

    private String scriptName;

    public DatabaseScriptSource(String scriptName){
        this.scriptName = scriptName;
    }


    @Override
    public String getScriptAsString() throws IOException {
        GroovyInfo groovyInfo = GroovyInnerCache.getByName(scriptName);
        if(groovyInfo!=null){
            return groovyInfo.getGroovyContent();
        }else {
            return "";
        }
    }

    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * e.g. "mypath/myfile.txt" -> "mypath/myfile".
     *
     */
    @Override
    public String suggestedClassName() {
        return StringUtils.stripFilenameExtension(this.scriptName);
    }
}
