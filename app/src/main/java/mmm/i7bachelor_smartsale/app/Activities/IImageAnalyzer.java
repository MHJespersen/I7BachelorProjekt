package mmm.i7bachelor_smartsale.app.Activities;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

public interface IImageAnalyzer {
    public void analyze(@NonNull ImageProxy image, int degrees);
}
