package com.checkpointBlog.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    
    /**
     * Sube un archivo a Cloudinary y devuelve los resultados completos
     * @param file Archivo a subir
     * @return Mapa con todos los metadatos de la imagen subida
     * @throws Exception Si ocurre algún error durante la subida
     */
    public Map<?, ?> uploadFile(MultipartFile file) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("folder", "checkpointBlog");
        params.put("upload_preset", "ml_default");
        params.put("resource_type", "auto"); // Para soportar imágenes y otros tipos
        
        return cloudinary.uploader().upload(file.getBytes(), params);
    }
    
    /**
     * Sube un archivo y devuelve solo la URL segura
     * @param file Archivo a subir
     * @return URL segura de la imagen
     * @throws Exception Si ocurre algún error durante la subida
     */
    public String uploadFileAndGetUrl(MultipartFile file) throws Exception {
        Map<?, ?> uploadResult = uploadFile(file);
        return uploadResult.get("secure_url").toString();
    }
    
    /**
     * Elimina una imagen de Cloudinary usando su public_id
     * @param publicId ID público de la imagen en Cloudinary
     * @return Resultado de la operación de eliminación
     * @throws Exception Si ocurre algún error durante la eliminación
     */
    public Map<?, ?> deleteImage(String publicId) throws Exception {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    
    /**
     * Obtiene información detallada de una imagen almacenada
     * @param publicId ID público de la imagen en Cloudinary
     * @return Mapa con la información de la imagen
     * @throws Exception Si ocurre algún error al obtener la información
     */
    public Map<?, ?> getImageInfo(String publicId) throws Exception {
        return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
    }
}