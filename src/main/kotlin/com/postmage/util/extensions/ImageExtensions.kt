package com.postmage.util.extensions

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

fun ByteArray.toBase64(): String =
    String(Base64.getEncoder().encode(this))

@Deprecated("Kullanıcıya fotoğraf gönderirken kullanma! İşlemleri çok uzatıyor.")
fun File.compressImage(image: File): ByteArray {
    println("Original Photo Byte Size: " + image.readBytes().size)
    val inputStream: InputStream = image.inputStream()
    val outputStream = ByteArrayOutputStream()
    val imageQuality = 0.3f

    // Create the buffered image
    val bufferedImage = ImageIO.read(inputStream)

    // Get image writers
    val imageWriters = ImageIO.getImageWritersByFormatName("jpg") // Input your Format Name here
    check(imageWriters.hasNext()) { "Writers Not Found!!" }
    val imageWriter = imageWriters.next()
    val imageOutputStream = ImageIO.createImageOutputStream(outputStream)
    imageWriter.output = imageOutputStream
    val imageWriteParam = imageWriter.defaultWriteParam

    // Set the compress quality metrics
    imageWriteParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
    imageWriteParam.compressionQuality = imageQuality

    // Compress and insert the image into the byte array.
    imageWriter.write(null, IIOImage(bufferedImage, null, null), imageWriteParam)
    val imageBytes: ByteArray = outputStream.toByteArray()

    // close all streams
    inputStream.close()
    outputStream.close()
    imageOutputStream.close()
    imageWriter.dispose()

    println("Compress Photo Byte Size: " + imageBytes.size)
    return imageBytes
}