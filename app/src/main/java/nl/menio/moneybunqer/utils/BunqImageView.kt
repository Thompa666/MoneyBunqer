package nl.menio.moneybunqer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.bunq.sdk.model.generated.`object`.Image

class BunqImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val attachmentManager = AttachmentManager.getInstance()

    fun setBunqImage(image: Image) {
        val attachmentPublicUuid = image.attachmentPublicUuid
        setAttachmentPublicUuid(attachmentPublicUuid)
    }

    fun setAttachmentPublicUuid(attachmentPublicUuid: String) {
        attachmentManager.getAttachmentBitmap(attachmentPublicUuid, object : AttachmentManager.OnGetAttachmentPublicContentListener {
            override fun onGetAttachmentPublicContentSuccess(bitmap: Bitmap) {
                setImageBitmap(bitmap)
            }

            override fun onGetAttachmentPublicContentError() {
                Log.e(TAG, "Could not load bunq image from UUID: $attachmentPublicUuid")
            }
        })
    }

    companion object {
        val TAG: String = BunqImageView::class.java.simpleName
    }
}