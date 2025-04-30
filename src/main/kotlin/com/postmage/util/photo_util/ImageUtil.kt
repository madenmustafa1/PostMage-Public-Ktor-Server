package com.postmage.util.photo_util

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO


object ImageUtil {

    suspend fun resizeImage(
        inputFile: File,
        ratio: Double
    ): ByteArray = withContext(Dispatchers.IO) {

        val originalImage: BufferedImage = ImageIO.read(inputFile)
        val originalWidth = originalImage.width
        val originalHeight = originalImage.height

        val newWidth = (originalWidth * ratio).toInt()
        val newHeight = (originalHeight * ratio).toInt()

        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = resizedImage.createGraphics()

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
        g.dispose()

        val outputStream = ByteArrayOutputStream()

        ImageIO.write(rotate(resizedImage, inputFile), "png", outputStream)
        return@withContext outputStream.toByteArray()
    }

    private fun rotate(image: BufferedImage, imageFile: File): BufferedImage {
        val orientation = getOrientation(imageFile)

        val width = image.width
        val height = image.height

        val newWidth: Int
        val newHeight: Int
        when (orientation) {
            6, 8 -> {
                newWidth = height
                newHeight = width
            }
            else -> {
                newWidth = width
                newHeight = height
            }
        }

        val newImage = BufferedImage(newWidth, newHeight, image.type)

        val g2 = newImage.createGraphics()

        /*
        @link https://stackoverflow.com/a/29319713/15381986
        1 -> [Exif IFD0] Orientation - Top, left side (Horizontal / normal)
        6 -> [Exif IFD0] Orientation - Right side, top (Rotate 90 CW)
        3 -> [Exif IFD0] Orientation - Bottom, right side (Rotate 180)
        8 -> [Exif IFD0] Orientation - Left side, bottom (Rotate 270 CW)
        */
        when (orientation) {
            1 -> {}
            6 -> {
                g2.rotate(Math.toRadians(90.0), (newWidth / 2).toDouble(), (newHeight / 2).toDouble())
                g2.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0)
            }
            3 -> {
                g2.rotate(Math.toRadians(180.0), (newWidth / 2).toDouble(), (newHeight / 2).toDouble())
                g2.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0)
            }
            8 -> {
                g2.rotate(Math.toRadians(270.0), (newWidth / 2).toDouble(), (newHeight / 2).toDouble())
                g2.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0)
            }
        }

        g2.drawImage(image, null, 0, 0)
        g2.dispose()

        return newImage
    }

    /**
     * @see https://github.com/drewnoakes/metadata-extractor
     */
    private fun getOrientation(inputFile: File): Int {
        val metadata = ImageMetadataReader.readMetadata(inputFile)
        val exifIFD0: ExifIFD0Directory = metadata.getDirectory(ExifIFD0Directory::class.java)
        return exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION)
    }


    suspend fun byteArrayToFile(
        byteArray: ByteArray,
        fileName: String
    ): File = withContext(Dispatchers.IO) {
        val file = File(fileName)
        val outputStream = FileOutputStream(file)
        outputStream.write(byteArray)
        outputStream.close()
        return@withContext file
    }

}