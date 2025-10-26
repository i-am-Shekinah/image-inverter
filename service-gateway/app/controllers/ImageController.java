package controllers;

import play.mvc.*;
import play.libs.Files.TemporaryFile;
import services.ImageInverterService;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.nio.file.Files;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller responsible for handling image uploads and retrievals.
 */
@Api(tags = "Image Operations")
public class ImageController extends Controller {

    private final ImageInverterService inverterService;

    @Inject
    public ImageController(ImageInverterService inverterService) {
        this.inverterService = inverterService;
    }

    /**
     * Endpoint to upload an image, invert its color using Akka, and save it.
     *
     * @param request the HTTP request containing the image file
     * @return success or error message
     */
    @ApiOperation(
            value = "Upload and invert an image",
            notes = "Uploads an image file, processes it asynchronously using Akka to invert colors (R→G, G→B, B→R), and saves it in the inverted images directory."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image uploaded and inverted successfully"),
            @ApiResponse(code = 400, message = "Invalid or missing image file"),
            @ApiResponse(code = 500, message = "Internal server error during processing")
    })
    public CompletionStage<Result> uploadImage(Http.Request request) {
        Http.MultipartFormData<TemporaryFile> formData = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> filePart = formData.getFile("image");

        if (filePart == null) {
            return CompletableFuture.completedFuture(badRequest("No image file uploaded"));
        }

        String fileName = filePart.getFilename();
        File uploadsDir = new File("public/images/uploads");
        File invertedDir = new File("public/images/inverted");
        uploadsDir.mkdirs();
        invertedDir.mkdirs();

        try {
            TemporaryFile tempFile = filePart.getFile();
            File uploadedFile = new File(uploadsDir, fileName);


            Files.copy(
                    tempFile.path(),
                    uploadedFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );


            File outputFile = new File(invertedDir, fileName);

            // Use Akka to process the image concurrently
            return inverterService.invertImage(uploadedFile, outputFile)
                    .thenApply(done -> ok("Image uploaded and inverted successfully: " + fileName))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return internalServerError("Error processing image: " + ex.getMessage());
                    });

        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(
                    internalServerError("Error saving uploaded image: " + e.getMessage())
            );
        }
    }

    /**
     * Endpoint to retrieve the inverted image by its filename.
     *
     * @param fileName the name of the inverted image file
     * @return the image file if found, 404 otherwise
     */
    @ApiOperation(
            value = "Retrieve an inverted image",
            notes = "Fetches the inverted image from the saved directory using the provided filename."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image retrieved successfully"),
            @ApiResponse(code = 404, message = "Image not found")
    })
    public Result getImage(String fileName) {
        File invertedFile = new File("public/images/inverted/" + fileName);
        if (!invertedFile.exists()) {
            return notFound("Image not found: " + fileName);
        }
        return ok(invertedFile);
    }
}
