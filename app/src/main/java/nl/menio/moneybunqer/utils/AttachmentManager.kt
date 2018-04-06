package nl.menio.moneybunqer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import nl.menio.moneybunqer.MoneyBunqerConfiguration
import nl.menio.moneybunqer.MoneyBunqerConfiguration.CLEAR_DATA_ON_START
import nl.menio.moneybunqer.network.BunqConnector
import st.lowlevel.storo.Storo
import st.lowlevel.storo.StoroBuilder
import java.util.concurrent.TimeUnit

class AttachmentManager(private val context: Context) {

    private val bunqConnector = BunqConnector.getInstance()
    private val handler = Handler()
    private val processingObjects = HashMap<String, HashSet<OnGetAttachmentPublicContentListener>>()

    init {
        StoroBuilder.configure(MoneyBunqerConfiguration.CACHE_SIZE_BYTES)
                .setDefaultCacheDirectory(context)
                .initialize()
        if (CLEAR_DATA_ON_START) {
            //Storo.clear()
        }
    }

    private fun processWaitingListeners(objectKey: String, bytes: ByteArray) {
        val listeners = processingObjects.get(objectKey)
        processingObjects.remove(objectKey)
        if (listeners != null) {
            for (listener in listeners) {
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                listener.onGetAttachmentPublicContentSuccess(bitmap)
            }
        }
    }

    private fun hasWaitingListeners(objectKey: String) : Boolean {
        val listeners = processingObjects[objectKey]
        return listeners != null && listeners.isNotEmpty()
    }

    private fun queueWaitingListener(objectKey: String, listener: OnGetAttachmentPublicContentListener) {
        val existingListeners = processingObjects[objectKey]
        val newListeners: HashSet<OnGetAttachmentPublicContentListener>
        if (existingListeners == null) {
            newListeners = HashSet()
        } else {
            newListeners = existingListeners
        }
        newListeners.add(listener)
        processingObjects.set(objectKey, newListeners)
    }

    fun getAttachmentBitmap(attachmentPublicUuid: String, listener: OnGetAttachmentPublicContentListener) {
        val objectKey = "$PREFIX_BITMAP_CACHE.$attachmentPublicUuid"
        val exists = Storo.contains(objectKey)
        val hasExpired = Storo.hasExpired(objectKey).execute() ?: true
        if (exists && !hasExpired) {
            val bytes = Storo.get(objectKey, ByteArray::class.java).execute()
            queueWaitingListener(objectKey, listener)
            processWaitingListeners(objectKey, bytes)
        } else if (hasWaitingListeners(objectKey)) {
            queueWaitingListener(objectKey, listener)
            //processWaitingListeners(objectKey)
        } else {
            bunqConnector.getAttachmentPublicContent(attachmentPublicUuid, object : BunqConnector.OnGetAttachmentPublicContentListener {
                override fun onGetAttachmentPublicContentSuccess(bytes: ByteArray) {
                    Storo.put("$PREFIX_BITMAP_CACHE.$attachmentPublicUuid", bytes)
                            .setExpiry(MoneyBunqerConfiguration.CACHE_BITMAP_EXPIRY_HOURS, TimeUnit.HOURS)
                            .execute()
                    processWaitingListeners(objectKey, bytes)
                }

                override fun onGetAttachmentPublicContentTooManyRequestsError(attachmentPublicUuid: String) {
                    handler.postDelayed({
                        getAttachmentBitmap(attachmentPublicUuid, listener)
                    }, DELAY_BETWEEN_CALLS_MILLISECONDS)
                    queueWaitingListener(objectKey, listener)
                }

                override fun onGetAttachmentPublicContentError() {
                    listener.onGetAttachmentPublicContentError()
                }
            })
        }
    }

    companion object {
        val TAG: String = AttachmentManager::class.java.simpleName

        private const val DELAY_BETWEEN_CALLS_MILLISECONDS: Long = 1000
        private const val PREFIX_BITMAP_CACHE = "bitmap"

        @SuppressLint("StaticFieldLeak")
        private var singleton: AttachmentManager? = null

        fun init(context: Context) {
            singleton = AttachmentManager(context)
        }

        fun getInstance() : AttachmentManager {
            return singleton ?: throw RuntimeException("Not initialized!")
        }
    }

    interface OnGetAttachmentPublicContentListener {
        fun onGetAttachmentPublicContentSuccess(bitmap: Bitmap)
        fun onGetAttachmentPublicContentError()
    }
}