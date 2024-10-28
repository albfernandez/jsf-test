/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.faces.jetty;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;

import org.eclipse.jetty.util.resource.Resource;


/**
 * @author Nick Belaevski
 * 
 */
final class BadResource extends Resource {

    private static final long serialVersionUID = -731399783337789651L;
    
    private String path;

    public BadResource(String name) {
        this.path = name;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Instant lastModified() {
        return super.lastModified();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long length() {
        return -1;
    }

    /*MZ
    @Override
    public File getFile() {
        return null;
    }
    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(path);
    }

    @Override
    public OutputStream getOutputStream() throws IOException, SecurityException {
        throw new FileNotFoundException(path);
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }

    @Override
    public String[] list() {
        return null;
    }

    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        return new BadResource(this.path + '/' + path);
    }
    */

    @Override
    public String getName() {
        return path;
    }

    /*MZ
    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public void release() {
    }
    */

	@Override
	public Path getPath() {
		//MZ
		return Path.of(path);
	}

	@Override
	public boolean isReadable() {
		//MZ
		return true;
	}

	@Override
	public URI getURI() {
		//MZ
		return null;
	}

	@Override
	public String getFileName() {
		//MZ
		return getPath().getFileName().toString();
	}

	@Override
	public Resource resolve(String subUriPath) {
		//MZ
		return null;
	}

}
