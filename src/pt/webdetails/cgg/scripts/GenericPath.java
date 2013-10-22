/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.scripts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import pt.webdetails.cpf.repository.RepositoryAccess;

/**
 * @author dcleao
 */
public class GenericPath {
    
    private final String   path;
    private final PathType type;
    private final String   normalizedPath;
    
    public enum PathType
    {
        SYSTEM, REPOSITORY
    }
    
    public GenericPath(String path, PathType pathType)
    {
        assert path != null;
        assert pathType != null;
        
        this.path = path;
        this.type = pathType;
        
        this.normalizedPath = normalizePath(path);
    }
    
    private String normalizePath(String path)
    {
        return path.replaceAll("\\\\", "/").replaceAll("/+", "/");
    }
    
    public String getPath()
    {
        return this.path;
    }
    
    public String getNormalizedPath()
    {
        return this.normalizedPath;
    }
    
    public String getDir()
    {
        return this.normalizedPath.replaceAll("(.*/).*", "$1");
    }
    
    public String getName()
    {
        return this.normalizedPath.replaceAll("(.*/)(.*)", "$2");
    }
    
    public PathType getPathType()
    {
        return this.type;
    }
    
    public String getResolvedPath()
    {
        switch(this.type)
        {
            case REPOSITORY: return normalizedPath;
            case SYSTEM: return RepositoryAccess.getSolutionPath("/system/" + normalizedPath).replaceAll("/+", "/");
        }
        
        throw new EnumConstantNotPresentException(PathType.class, this.type.toString());
    }
    
    public Reader getReader() throws IOException
    {
        String fullPath = this.getResolvedPath();
        switch(this.type)
        {
            case REPOSITORY:
                if(!RepositoryAccess.getRepository().resourceExists(fullPath))
                {
                  throw new FileNotFoundException("Couldn't find repository file '" + fullPath + "'.");
                }
                
                return new StringReader(RepositoryAccess.getRepository().getResourceAsString(fullPath));
                
            case SYSTEM: return new FileReader(fullPath);
        }
        
        throw new EnumConstantNotPresentException(PathType.class, this.type.toString());
    }
    
    public GenericPath getBasePath()
    {
        return new GenericPath(this.getDir(), this.type);
    }
}
