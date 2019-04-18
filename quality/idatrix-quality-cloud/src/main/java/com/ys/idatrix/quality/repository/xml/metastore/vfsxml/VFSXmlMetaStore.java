/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package com.ys.idatrix.quality.repository.xml.metastore.vfsxml;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.metastore.api.BaseMetaStore;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreAttribute;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreDependenciesExistsException;
import org.pentaho.metastore.api.exceptions.MetaStoreElementExistException;
import org.pentaho.metastore.api.exceptions.MetaStoreElementTypeExistsException;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.api.exceptions.MetaStoreNamespaceExistsException;
import org.pentaho.metastore.api.security.IMetaStoreElementOwner;
import org.pentaho.metastore.api.security.MetaStoreElementOwnerType;
import org.pentaho.metastore.stores.xml.AutomaticXmlMetaStoreCache;
import org.pentaho.metastore.stores.xml.XmlMetaStoreAttribute;
import org.pentaho.metastore.stores.xml.XmlMetaStoreCache;
import org.pentaho.metastore.stores.xml.XmlMetaStoreElementOwner;
import org.pentaho.metastore.stores.xml.XmlUtil;

import com.ys.idatrix.quality.ext.utils.UnixPathUtil;

public class VFSXmlMetaStore extends BaseMetaStore implements IMetaStore {

	private String rootFolder;

	private FileObject rootFile;

	private final XmlMetaStoreCache metaStoreCache;

	public VFSXmlMetaStore() throws MetaStoreException {
		this(new AutomaticXmlMetaStoreCache());
	}

	public VFSXmlMetaStore(XmlMetaStoreCache metaStoreCacheImpl) throws MetaStoreException {
		this(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID(), metaStoreCacheImpl);
	}

	public VFSXmlMetaStore(String rootFolder) throws MetaStoreException {
		this(rootFolder, new AutomaticXmlMetaStoreCache());
	}

	public VFSXmlMetaStore(String rootFolder, XmlMetaStoreCache metaStoreCacheImpl) throws MetaStoreException {
		this.rootFolder = rootFolder + File.separator + XmlUtil.META_FOLDER_NAME;

		try {
			rootFile = KettleVFS.getFileObject(this.rootFolder);
			if (!rootFile.exists()) {
				rootFile.createFolder();
				if (!rootFile.exists()) {
					rootFile.close();
					throw new MetaStoreException("Unable to create XML meta store root folder: " + this.rootFolder);
				}
			}
		} catch (Exception e) {
			try {
				if (rootFile != null) {
					rootFile.close();
				}
			} catch (FileSystemException e1) {
			}
			throw new MetaStoreException("Unable to create XML meta store root folder: " + this.rootFolder);
		}
		// Give the MetaStore a default name
		//
		setName(this.rootFolder);
		metaStoreCache = metaStoreCacheImpl;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof VFSXmlMetaStore)) {
			return false;
		}
		return ((VFSXmlMetaStore) obj).name.equalsIgnoreCase(name);
	}

	@Override
	public synchronized List<String> getNamespaces() throws MetaStoreException {
		lockStore();
		try {
			FileObject[] files = listFolders(rootFile);
			List<String> namespaces = new ArrayList<String>(files.length);
			for (FileObject file : files) {
				namespaces.add(file.getName().getBaseName());
			}
			return namespaces;
		} finally {
			unlockStore();
		}
	}

	@Override
	public synchronized boolean namespaceExists(String namespace) throws MetaStoreException {
		lockStore();
		FileObject spaceFile = null;
		try {
			String spaceFolder = XmlUtil.getNamespaceFolder(rootFolder, namespace);
			spaceFile = KettleVFS.getFileObject(spaceFolder);
			return spaceFile.exists();
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (spaceFile != null) {
				try {
					spaceFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized void createNamespace(String namespace)
			throws MetaStoreException, MetaStoreNamespaceExistsException {
		lockStore();
		FileObject spaceFile = null;
		try {
			String spaceFolder = XmlUtil.getNamespaceFolder(rootFolder, namespace);
			spaceFile = KettleVFS.getFileObject(spaceFolder);
			if (spaceFile.exists()) {
				throw new MetaStoreNamespaceExistsException(
						"The namespace with name '" + namespace + "' already exists.");
			}
			spaceFile.createFolder();
			if (!spaceFile.exists()) {
				throw new MetaStoreException("Unable to create XML meta store namespace folder: " + spaceFolder);
			}
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (spaceFile != null) {
				try {
					spaceFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized void deleteNamespace(String namespace)
			throws MetaStoreException, MetaStoreElementTypeExistsException {
		lockStore();
		FileObject spaceFile = null;
		try {
			String spaceFolder = XmlUtil.getNamespaceFolder(rootFolder, namespace);
			spaceFile = KettleVFS.getFileObject(spaceFolder);
			if (!spaceFile.exists()) {
				return; // Should we throw an exception?
			}
			List<IMetaStoreElementType> elementTypes = getElementTypes(namespace, false);

			if (!elementTypes.isEmpty()) {
				List<String> dependencies = new ArrayList<String>(elementTypes.size());
				for (IMetaStoreElementType elementType : elementTypes) {
					dependencies.add(elementType.getId());
				}
				throw new MetaStoreDependenciesExistsException(dependencies,
						"Unable to delete the XML meta store namespace with name '" + namespace
								+ "' as it still contains dependencies");
			}

			if (!spaceFile.delete()) {
				throw new MetaStoreException(
						"Unable to delete XML meta store namespace folder, check to see if it's empty");
			}
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (spaceFile != null) {
				try {
					spaceFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized List<IMetaStoreElementType> getElementTypes(String namespace) throws MetaStoreException {
		return getElementTypes(namespace, true);
	}

	protected synchronized List<IMetaStoreElementType> getElementTypes(String namespace, boolean lock)
			throws MetaStoreException {
		if (lock) {
			lockStore();
		}
		FileObject spaceFolderFile = null;
		try {
			String spaceFolder = XmlUtil.getNamespaceFolder(rootFolder, namespace);
			spaceFolderFile = KettleVFS.getFileObject(spaceFolder);
			FileObject[] elementTypeFolders = listFolders(spaceFolderFile);
			List<IMetaStoreElementType> elementTypes = new ArrayList<IMetaStoreElementType>(elementTypeFolders.length);
			for (FileObject elementTypeFolder : elementTypeFolders) {
				String elementTypeId = elementTypeFolder.getName().getBaseName();
				IMetaStoreElementType elementType = getElementType(namespace, elementTypeId, false);
				elementTypes.add(elementType);
			}

			return elementTypes;
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			if (lock) {
				unlockStore();
			}
			if (spaceFolderFile != null) {
				try {
					spaceFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized List<String> getElementTypeIds(String namespace) throws MetaStoreException {
		lockStore();
		FileObject spaceFolderFile = null;
		try {
			String spaceFolder = XmlUtil.getNamespaceFolder(rootFolder, namespace);
			spaceFolderFile = KettleVFS.getFileObject(spaceFolder);
			FileObject[] elementTypeFolders = listFolders(spaceFolderFile);
			List<String> ids = new ArrayList<String>(elementTypeFolders.length);
			for (FileObject elementTypeFolder : elementTypeFolders) {
				String elementTypeId = elementTypeFolder.getName().getBaseName();
				ids.add(elementTypeId);
			}

			return ids;
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (spaceFolderFile != null) {
				try {
					spaceFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	protected synchronized VfsxmlMetaStoreElementType getElementType(String namespace, String elementTypeId,
			boolean lock) throws MetaStoreException {
		if (lock) {
			lockStore();
		}
		try {
			String elementTypeFile = XmlUtil.getElementTypeFile(rootFolder, namespace, elementTypeId);
			VfsxmlMetaStoreElementType elementType = new VfsxmlMetaStoreElementType(namespace, elementTypeFile);
			elementType.setMetaStoreName(getName());
			return elementType;
		} finally {
			if (lock) {
				unlockStore();
			}
		}
	}

	public synchronized VfsxmlMetaStoreElementType getElementType(String namespace, String elementTypeId)
			throws MetaStoreException {
		return getElementType(namespace, elementTypeId, true);
	}

	@Override
	public synchronized VfsxmlMetaStoreElementType getElementTypeByName(String namespace, String elementTypeName)
			throws MetaStoreException {
		for (IMetaStoreElementType elementType : getElementTypes(namespace)) {
			if (elementType.getName() != null && elementType.getName().equalsIgnoreCase(elementTypeName)) {
				return (VfsxmlMetaStoreElementType) elementType;
			}
		}
		return null;
	}

	public IMetaStoreAttribute newAttribute(String id, Object value) throws MetaStoreException {
		return new XmlMetaStoreAttribute(id, value);
	}

	@Override
	public synchronized void createElementType(String namespace, IMetaStoreElementType elementType)
			throws MetaStoreException, MetaStoreElementTypeExistsException {
		lockStore();
		FileObject elementTypeFolderFile = null;
		try {
			// In the case of a file, the ID is the name
			//
			if (elementType.getId() == null) {
				elementType.setId(elementType.getName());
			}

			String elementTypeFolder = XmlUtil.getElementTypeFolder(rootFolder, namespace, elementType.getName());
			elementTypeFolderFile = KettleVFS.getFileObject(elementTypeFolder);
			if (elementTypeFolderFile.exists()) {
				throw new MetaStoreElementTypeExistsException(getElementTypes(namespace, false),
						"The specified element type already exists with the same ID");
			}
			elementTypeFolderFile.createFolder();
			if (!elementTypeFolderFile.exists()) {
				throw new MetaStoreException(
						"Unable to create XML meta store element type folder '" + elementTypeFolder + "'");
			}

			String elementTypeFilename = XmlUtil.getElementTypeFile(rootFolder, namespace, elementType.getName());

			// Copy the element type information to the XML meta store
			//
			VfsxmlMetaStoreElementType xmlType = new VfsxmlMetaStoreElementType(namespace, elementType.getId(),
					elementType.getName(), elementType.getDescription());
			xmlType.setFilename(elementTypeFilename);
			xmlType.save();

			metaStoreCache.registerElementTypeIdForName(namespace, elementType.getName(), elementType.getId());
			metaStoreCache.registerProcessedFile(elementTypeFolder,
					elementTypeFolderFile.getContent().getLastModifiedTime());

			xmlType.setMetaStoreName(getName());
			elementType.setMetaStoreName(getName());
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementTypeFolderFile != null) {
				try {
					elementTypeFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized void updateElementType(String namespace, IMetaStoreElementType elementType)
			throws MetaStoreException {
		lockStore();
		FileObject elementTypeFolderFile = null;
		try {
			String elementTypeFolder = XmlUtil.getElementTypeFolder(rootFolder, namespace, elementType.getName());
			elementTypeFolderFile = KettleVFS.getFileObject(elementTypeFolder);
			if (!elementTypeFolderFile.exists()) {
				throw new MetaStoreException("The specified element type with ID '" + elementType.getId()
						+ "' doesn't exists so we can't update it.");
			}

			String elementTypeFilename = XmlUtil.getElementTypeFile(rootFolder, namespace, elementType.getName());

			// Save the element type information to the XML meta store
			//
			VfsxmlMetaStoreElementType xmlType = new VfsxmlMetaStoreElementType(namespace, elementType.getId(),
					elementType.getName(), elementType.getDescription());
			xmlType.setFilename(elementTypeFilename);
			xmlType.save();

			metaStoreCache.registerElementTypeIdForName(namespace, elementType.getName(), elementType.getId());
			metaStoreCache.registerProcessedFile(elementTypeFolder,
					elementTypeFolderFile.getContent().getLastModifiedTime());
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementTypeFolderFile != null) {
				try {
					elementTypeFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized void deleteElementType(String namespace, IMetaStoreElementType elementType)
			throws MetaStoreException, MetaStoreDependenciesExistsException {
		lockStore();
		FileObject elementTypeFile = null;
		try {
			String elementTypeFilename = XmlUtil.getElementTypeFile(rootFolder, namespace, elementType.getName());
			elementTypeFile = KettleVFS.getFileObject(elementTypeFilename);
			if (!elementTypeFile.exists()) {
				return;
			}
			// Check if the element type has no remaining elements
			List<IMetaStoreElement> elements = getElements(namespace, elementType, false, true);
			if (!elements.isEmpty()) {
				List<String> dependencies = new ArrayList<String>();
				for (IMetaStoreElement element : elements) {
					dependencies.add(element.getId());
				}
				throw new MetaStoreDependenciesExistsException(dependencies,
						"Unable to delete element type with name '" + elementType.getName() + "' in namespace '"
								+ namespace + "' because there are still elements present");
			}

			// Remove the elementType.xml file
			//
			if (!elementTypeFile.delete()) {
				throw new MetaStoreException("Unable to delete element type XML file '" + elementTypeFilename + "'");
			}

			// Remove the folder too, should be empty by now.
			//
			String elementTypeFolder = XmlUtil.getElementTypeFolder(rootFolder, namespace, elementType.getName());
			FileObject elementTypeFolderFile = KettleVFS.getFileObject(elementTypeFolder);
			if (!elementTypeFolderFile.delete()) {
				elementTypeFolderFile.close();
				throw new MetaStoreException("Unable to delete element type XML folder '" + elementTypeFolder + "'");
			}
			metaStoreCache.unregisterElementTypeId(namespace, elementType.getId());
			metaStoreCache.unregisterProcessedFile(elementTypeFolder);
			elementTypeFolderFile.close();
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementTypeFile != null) {
				try {
					elementTypeFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public List<IMetaStoreElement> getElements(String namespace, IMetaStoreElementType elementType)
			throws MetaStoreException {
		return getElements(namespace, elementType, true, true);
	}

	protected synchronized List<IMetaStoreElement> getElements(String namespace, IMetaStoreElementType elementType,
			boolean lock, boolean includeProcessedFiles) throws MetaStoreException {
		if (lock) {
			lockStore();
		}
		FileObject elementTypeFolderFile = null;
		try {
			String elementTypeFolder = XmlUtil.getElementTypeFolder(rootFolder, namespace, elementType.getName());
			elementTypeFolderFile = KettleVFS.getFileObject(elementTypeFolder);
			FileObject[] elementTypeFiles = listFiles(elementTypeFolderFile, includeProcessedFiles);
			List<IMetaStoreElement> elements = new ArrayList<IMetaStoreElement>(elementTypeFiles.length);
			for (FileObject elementTypeFile : elementTypeFiles) {
				String elementId = elementTypeFile.getName().getBaseName();
				// File .type.xml doesn't hidden in OS Windows so better to ignore it explicitly
				if (elementId.equals(XmlUtil.ELEMENT_TYPE_FILE_NAME)) {
					continue;
				}
				elementId = elementId.substring(0, elementId.length() - 4); // remove .xml to get the ID
				elements.add(getElement(namespace, elementType, elementId, false));
			}

			return elements;
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			if (lock) {
				unlockStore();
			}
			if (elementTypeFolderFile != null) {
				try {
					elementTypeFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized List<String> getElementIds(String namespace, IMetaStoreElementType elementType)
			throws MetaStoreException {
		lockStore();
		FileObject elementTypeFolderFile = null;
		try {
			String elementTypeFolder = XmlUtil.getElementTypeFolder(rootFolder, namespace, elementType.getName());
			elementTypeFolderFile = KettleVFS.getFileObject(elementTypeFolder);
			FileObject[] elementTypeFiles = listFiles(elementTypeFolderFile, true);
			List<String> elementIds = new ArrayList<String>(elementTypeFiles.length);
			for (FileObject elementTypeFile : elementTypeFiles) {
				String elementId = elementTypeFile.getName().getBaseName();
				// File .type.xml doesn't hidden in OS Windows so better to ignore it explicitly
				if (elementId.equals(XmlUtil.ELEMENT_TYPE_FILE_NAME)) {
					continue;
				}
				elementId = elementId.substring(0, elementId.length() - 4); // remove .xml to get the ID
				elementIds.add(elementId);
			}

			return elementIds;
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementTypeFolderFile != null) {
				try {
					elementTypeFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public IMetaStoreElement getElement(String namespace, IMetaStoreElementType elementType, String elementId)
			throws MetaStoreException {
		return getElement(namespace, elementType, elementId, true);
	}

	protected synchronized IMetaStoreElement getElement(String namespace, IMetaStoreElementType elementType,
			String elementId, boolean lock) throws MetaStoreException {
		if (lock) {
			lockStore();
		}
		FileObject elementFile = null;
		try {
			String elementFilename = XmlUtil.getElementFile(rootFolder, namespace, elementType.getName(), elementId);
			elementFile = KettleVFS.getFileObject(elementFilename);
			if (!elementFile.exists()) {
				return null;
			}
			VfsxmlMetaStoreElement element = new VfsxmlMetaStoreElement(elementFilename);
			metaStoreCache.registerElementIdForName(namespace, elementType, element.getName(), elementId);
			metaStoreCache.registerProcessedFile(elementFilename, elementFile.getContent().getLastModifiedTime());
			return element;
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			if (lock) {
				unlockStore();
			}
			if (elementFile != null) {
				try {
					elementFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized IMetaStoreElement getElementByName(String namespace, IMetaStoreElementType elementType,
			String name) throws MetaStoreException {
		lockStore();
		try {
			String chachedElementId = metaStoreCache.getElementIdByName(namespace, elementType, name);
			if (chachedElementId != null) {
				IMetaStoreElement element = getElement(namespace, elementType, chachedElementId, false);
				if (element != null && element.getName().equalsIgnoreCase(name)) {
					return element;
				}
			}

			for (IMetaStoreElement element : getElements(namespace, elementType, false, false)) {
				if (element.getName() != null && element.getName().equalsIgnoreCase(name)) {
					return element;
				}
			}
			return null;
		} finally {
			unlockStore();
		}
	}

	public synchronized void createElement(String namespace, IMetaStoreElementType elementType,
			IMetaStoreElement element) throws MetaStoreException, MetaStoreElementExistException {
		lockStore();
		FileObject elementFile = null;
		try {
			// In the case of a file, the ID is the name
			//
			if (element.getId() == null) {
				element.setId(element.getName());
			}

			String elementFilename = XmlUtil.getElementFile(rootFolder, namespace, elementType.getName(),
					element.getId());
			elementFile = KettleVFS.getFileObject(elementFilename);
			if (elementFile.exists()) {
				throw new MetaStoreElementExistException(getElements(namespace, elementType, false, true),
						"The specified element already exists with the same ID: '" + element.getId() + "'");
			}
			VfsxmlMetaStoreElement xmlElement = new VfsxmlMetaStoreElement(element);
			xmlElement.setFilename(elementFilename);
			xmlElement.save();

			metaStoreCache.registerElementIdForName(namespace, elementType, xmlElement.getName(), element.getId());
			metaStoreCache.registerProcessedFile(elementFilename, elementFile.getContent().getLastModifiedTime());
			// In the case of the XML store, the name is the same as the ID
			//
			element.setId(xmlElement.getName());
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementFile != null) {
				try {
					elementFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized void updateElement(String namespace, IMetaStoreElementType elementType, String elementId,
			IMetaStoreElement element) throws MetaStoreException {

		// verify that the element type belongs to this meta store
		//
		if (elementType.getMetaStoreName() == null || !elementType.getMetaStoreName().equals(getName())) {
			throw new MetaStoreException("The element type '" + elementType.getName()
					+ "' needs to explicitly belong to the meta store in which you are updating.");
		}

		lockStore();
		FileObject elementFile = null;
		try {
			String elementFilename = XmlUtil.getElementFile(rootFolder, namespace, elementType.getName(),
					element.getName());
			elementFile = KettleVFS.getFileObject(elementFilename);
			if (!elementFile.exists()) {
				throw new MetaStoreException(
						"The specified element to update doesn't exist with ID: '" + elementId + "'");
			}

			VfsxmlMetaStoreElement xmlElement = new VfsxmlMetaStoreElement(element);
			xmlElement.setFilename(elementFilename);
			xmlElement.setIdWithFilename(elementFilename);
			xmlElement.save();

			metaStoreCache.registerElementIdForName(namespace, elementType, xmlElement.getName(), xmlElement.getId());
			metaStoreCache.registerProcessedFile(elementFilename, elementFile.getContent().getLastModifiedTime());
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementFile != null) {
				try {
					elementFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	@Override
	public synchronized void deleteElement(String namespace, IMetaStoreElementType elementType, String elementId)
			throws MetaStoreException {
		lockStore();
		FileObject elementFile = null;
		try {
			String elementFilename = XmlUtil.getElementFile(rootFolder, namespace, elementType.getName(), elementId);
			elementFile = KettleVFS.getFileObject(elementFilename);
			if (!elementFile.exists()) {
				return;
			}

			if (!elementFile.delete()) {
				throw new MetaStoreException(
						"Unable to delete element with ID '" + elementId + "' in filename '" + elementFilename + "'");
			}

			metaStoreCache.unregisterElementId(namespace, elementType, elementId);
			metaStoreCache.unregisterProcessedFile(elementFilename);
		} catch (Exception e) {
			throw new MetaStoreException(e);
		} finally {
			unlockStore();
			if (elementFile != null) {
				try {
					elementFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	/**
	 * @return the rootFolder
	 */
	public String getRootFolder() {
		return rootFolder;
	}

	/**
	 * @param rootFolder
	 *            the rootFolder to set
	 */
	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * @param folder
	 * @return the non-hidden folders in the specified folder
	 */
	protected FileObject[] listFolders(FileObject folder) {

		try {
			FileObject[] children = folder.getChildren();
			if (children == null || children.length == 0) {
				return new FileObject[] {};
			}
			List<FileObject> folders = Arrays.asList(children).stream().filter(file -> {
				try {
					return !file.isHidden() && file.isFolder();
				} catch (FileSystemException e) {
					return false;
				}
			}).collect(Collectors.toList());

			if (folders == null) {
				return new FileObject[] {};
			}

			return folders.toArray(new FileObject[] {});
		} catch (FileSystemException e) {
		}
		return new FileObject[] {};
	}

	/**
	 * @param folder
	 * @param includeProcessedFiles
	 * @return the non-hidden files in the specified folder
	 */
	protected FileObject[] listFiles(FileObject folder, final boolean includeProcessedFiles) {
		try {
			FileObject[] children = folder.getChildren();
			if (children == null || children.length == 0) {
				return new FileObject[] {};
			}
			List<FileObject> folders = Arrays.asList(children).stream().filter(file -> {
				try {
					if (!includeProcessedFiles) {
						Map<String, Long> processedFiles = metaStoreCache.getProcessedFiles();
						Long fileLastModified = processedFiles.get(file.getName().getPath());
						if (fileLastModified != null
								&& fileLastModified.equals(file.getContent().getLastModifiedTime())) {
							return false;
						}
					}
					return !file.isHidden() && file.isFile();
				} catch (FileSystemException e) {
					return false;
				}
			}).collect(Collectors.toList());

			if (folders == null) {
				return new FileObject[] {};
			}

			return folders.toArray(new FileObject[] {});
		} catch (FileSystemException e) {
		}
		return new FileObject[] {};
	}

	@Override
	public IMetaStoreElementType newElementType(String namespace) throws MetaStoreException {
		return new VfsxmlMetaStoreElementType(namespace, null, null, null);
	}

	@Override
	public IMetaStoreElement newElement() throws MetaStoreException {
		return new VfsxmlMetaStoreElement();
	}

	@Override
	public IMetaStoreElement newElement(IMetaStoreElementType elementType, String id, Object value)
			throws MetaStoreException {
		return new VfsxmlMetaStoreElement(elementType, id, value);
	}

	@Override
	public IMetaStoreElementOwner newElementOwner(String name, MetaStoreElementOwnerType ownerType)
			throws MetaStoreException {
		return new XmlMetaStoreElementOwner(name, ownerType);
	}

	/**
	 * Create a .lock file in the store root folder. If it already exists, wait
	 * until it becomes available.
	 * 
	 * @throws MetaStoreException
	 *             in case we have to wait more than 10 seconds to acquire a lock
	 */
	protected void lockStore() throws MetaStoreException {
		boolean waiting = true;
		long totalTime = 0L;
		FileObject lockFile = null;
		try {
			lockFile = KettleVFS.getFileObject(rootFile + File.separator + ".lock");
			while (waiting) {
				try {
					if (!lockFile.exists()) {
						lockFile.createFile();
						lockFile.close();
						return;
					}
				} catch (Exception e) {
					throw new MetaStoreException("Unable to create lock file: " + lockFile.toString(), e);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				totalTime += 100;
				if (totalTime > 10000) {
					throw new MetaStoreException("Maximum wait time of 10 seconds exceed while acquiring lock");
				}
			}
		} catch (Exception e) {
			throw new MetaStoreException("Unable to create lock file: " + lockFile.toString(), e);
		} finally {
			if (lockFile != null) {
				try {
					lockFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	protected void unlockStore() throws MetaStoreException {
		FileObject lockFile = null;
		try {
			lockFile = KettleVFS.getFileObject(rootFile + File.separator + ".lock");
			lockFile.delete();
		} catch (Exception e) {
			throw new MetaStoreException("Unable to delete lock file: " + lockFile.toString(), e);
		} finally {
			if (lockFile != null) {
				try {
					lockFile.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}

	public static IMetaStore createMetaStore(String rootPath , boolean allowCreate) throws MetaStoreException {

		String rootFolder = Const.NVL( Const.NVL(rootPath,  System.getProperty(Const.PENTAHO_METASTORE_FOLDER) ),(Const.getUserHomeDirectory() + File.separator + ".pentaho") );
		FileObject rootFolderFile = null ;
		FileObject metaFolder = null ;
		try {
			rootFolderFile = KettleVFS.getFileObject(UnixPathUtil.unixPath(rootFolder));
			metaFolder = KettleVFS.getFileObject(UnixPathUtil.unixPath(rootFolder) + File.separator + XmlUtil.META_FOLDER_NAME);
			if (!allowCreate && !metaFolder.exists()) {
				return null;
			}
			if (!rootFolderFile.exists()) {
				rootFolderFile.createFolder();
			}
		} catch ( Exception e) {
			throw new MetaStoreException(e);
		} finally {
			if (rootFolderFile != null) {
				try {
					rootFolderFile.close();
				} catch (FileSystemException e) {
				}
			}
			if (metaFolder != null) {
				try {
					metaFolder.close();
				} catch (FileSystemException e) {
				}
			}
		}

		VFSXmlMetaStore metaStore = new VFSXmlMetaStore(UnixPathUtil.unixPath(rootFolder));
		if (allowCreate) {
			metaStore.setName(Const.PENTAHO_METASTORE_NAME);
		}
		return metaStore;
	}

}
