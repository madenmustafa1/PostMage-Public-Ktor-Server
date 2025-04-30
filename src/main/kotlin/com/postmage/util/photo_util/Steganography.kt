package com.postmage.util.photo_util

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object Steganography {

    /*
    FileExtensions ->
      Steganography.execute(
        photoFile = photoDir,
        message = "hello world"
      )
     */

    fun execute(photoFile: File, message: String): String {
        val coverImage = ImageIO.read(photoFile)

        val embeddedImage = embedText(coverImage, message, photoFile.path)

        val outputFile = File(replacePath(photoFile.path))
        ImageIO.write(embeddedImage, "png", outputFile)
        println("embedded image success.")
        val extractText = extractText(embeddedImage, message.length)
        println("@@@@@@@@: $extractText")
        return extractText
    }

    private fun replacePath(path: String) = path.dropLast(4) + System.currentTimeMillis().toString() + path.takeLast(4)

    /**
    @see https://github.com/tigerlyb/Steganography-in-Java
     */
    private fun embedText(image: BufferedImage, text: String, path: String): BufferedImage {
        val bitMask = 0x00000001 // define the mask bit used to get the digit
        var bit: Int // define a integer number to represent the ASCII number of a character
        var x = 0 // define the starting pixel x
        var y = 0 // define the starting pixel y
        for (element in text) {
            bit = element.code // get the ASCII number of a character
            for (j in 0..7) {
                val flag = bit and bitMask // get 1 digit from the character
                if (flag == 1) {
                    if (x < image.width) {
                        image.setRGB(
                            x,
                            y,
                            image.getRGB(x, y) or 0x00000001
                        ) // store the bit which is 1 into a pixel's last digit
                        x++
                    } else {
                        x = 0
                        y++
                        image.setRGB(
                            x,
                            y,
                            image.getRGB(x, y) or 0x00000001
                        ) // store the bit which is 1 into a pixel's last digit
                    }
                } else {
                    if (x < image.width) {
                        image.setRGB(
                            x,
                            y,
                            image.getRGB(x, y) and -0x2
                        ) // store the bit which is 0 into a pixel's last digit
                        x++
                    } else {
                        x = 0
                        y++
                        image.setRGB(
                            x,
                            y,
                            image.getRGB(x, y) and -0x2
                        ) // store the bit which is 0 into a pixel's last digit
                    }
                }
                bit = bit shr 1 // get the next digit from the character
            }
        }

        // save the image which contains the secret information to another image file
        try {
            val outputfile = File(replacePath(path))
            ImageIO.write(image, "png", outputfile)
        } catch (_: IOException) {
        }
        return image
    }

    // extract secret information/Text from a "cover image"
    private fun extractText(image: BufferedImage, length: Int): String {
        val bitMask = 0x00000001 // define the mask bit used to get the digit
        var x = 0 // define the starting pixel x
        var y = 0 // define the starting pixel y
        var flag: Int
        val c = CharArray(length) // define a character array to store the secret information
        var message = ""
        for (i in 0 until length) {
            var bit = 0

            // 8 digits form a character
            for (j in 0..7) {
                if (x < image.width) {
                    flag = image.getRGB(x, y) and bitMask // get the last digit of the pixel
                    x++
                } else {
                    x = 0
                    y++
                    flag = image.getRGB(x, y) and bitMask // get the last digit of the pixel
                }

                // store the extracted digits into an integer as a ASCII number
                if (flag == 1) {
                    bit = bit shr 1
                    bit = bit or 0x80
                } else {
                    bit = bit shr 1
                }
            }
            c[i] = bit.toChar() // represent the ASCII number by characters
            message += c[i]
        }

        return message
    }

    // embed secret image into a "cover image"
    private fun embedImage(imageC: BufferedImage, imageS: BufferedImage): BufferedImage? {
        val bitMask = 0x00000001 // define the mask bit used to get the digit
        var x = 0 // define the starting pixel x
        var y = 0 // define the starting pixel y
        val l = imageS.width * imageS.height // calculate the total pixel in the secret image
        val array = IntArray(l) // define an array to store the color numbers from the secret image

        // store the RGB from the secret to an array
        for (i in array.indices) {
            if (x < imageS.width) {
                array[i] = imageS.getRGB(x, y)
                x++
            } else {
                x = 0
                y++
                array[i] = imageS.getRGB(x, y)
            }
        }
        x = 0 // reset the x coordinate of the cover image
        y = 0 // reset the y coordinate of the cover image
        for (i in 0 until l) {
            for (j in 0..31) {
                val flag = array[i] and bitMask // get 1 digit from the character
                if (flag == 1) {
                    if (x < imageC.width) {
                        imageC.setRGB(
                            x,
                            y,
                            imageC.getRGB(x, y) or 0x00000001
                        ) // store the bit which is 1 into a pixel's last digit
                        x++
                    } else {
                        x = 0
                        y++
                        imageC.setRGB(
                            x,
                            y,
                            imageC.getRGB(x, y) or 0x00000001
                        ) // store the bit which is 1 into a pixel's last digit
                    }
                } else {
                    if (x < imageC.width) {
                        imageC.setRGB(
                            x,
                            y,
                            imageC.getRGB(x, y) and -0x2
                        ) // store the bit which is 0 into a pixel's last digit
                        x++
                    } else {
                        x = 0
                        y++
                        imageC.setRGB(
                            x,
                            y,
                            imageC.getRGB(x, y) and -0x2
                        ) // store the bit which is 0 into a pixel's last digit
                    }
                }
                array[i] = array[i] shr 1 // get the next digit from the character
            }
        }

        // save the image which contains the secret information to another image file
        try {
            val outputfile = File("imageEmbedded.png")
            ImageIO.write(imageC, "png", outputfile)
        } catch (e: IOException) {
        }
        return imageC
    }

    // extract secret image from a "cover image"
    private fun extractImage(image: BufferedImage, width: Int, height: Int) {
        val bitMask = 0x00000001 // define the mask bit used to get the digit
        var x = 0 // define the starting pixel x
        var y = 0 // define the starting pixel y
        var flag: Int
        val imageStore = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val pixelNumber = width * height
        val array = IntArray(pixelNumber)
        for (i in 0 until pixelNumber) {
            var bit = 0x00000000

            // 32 digits form a pixel
            for (j in 0..31) {
                if (x < image.width) {
                    flag = image.getRGB(x, y) and bitMask // get the last digit of the pixel
                    x++
                } else {
                    x = 0
                    y++
                    flag = image.getRGB(x, y) and bitMask // get the last digit of the pixel
                }

                // store the extracted digits into an integer
                if (flag == 1) {
                    bit = bit shr 1
                    bit = bit or -0x80000000
                } else {
                    bit = bit shr 1
                    bit = bit and 0x7FFFFFFF
                }
            }
            array[i] = bit // store the integer color in to an array
            //System.out.println(Integer.toBinaryString(array[i]));
        }
        x = 0 // reset the x coordinate of the extracted image
        y = 0 // reset the y coordinate of the extracted image

        // draw the extracted image
        for (i in array.indices) {
            if (x < width) {
                imageStore.setRGB(x, y, array[i])
                x++
            } else {
                x = 0
                y++
                imageStore.setRGB(x, y, array[i])
            }
        }

        // store the extracted image
        try {
            val outputfile = File("imageExtract.png")
            ImageIO.write(imageStore, "png", outputfile)
        } catch (e: IOException) {
        }
    }


}