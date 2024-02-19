package com.example.animationsoundwave

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide

private var CardView.cardCornerRadius: Float
    get() = radius
    set(value) {
        radius = value
    }

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var frameLayout: RelativeLayout

    private lateinit var hintHandler: Handler
    private var typingIndex = 0
    private val typingDelay: Long = 150

    private lateinit var microphoneImageView: ImageView
    private lateinit var cardView: CardView

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var visualizer: Visualizer


    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sendButton: ImageView = findViewById(R.id.sendid)

        editText = findViewById(R.id.editText)
        frameLayout = findViewById(R.id.relativeLayout)
        hintHandler = Handler(Looper.getMainLooper())

        microphoneImageView = findViewById(R.id.mic)



        sendButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                addMovableText(text)
                editText.text.clear() // Clear the EditText for new input
            }
        }

        microphoneImageView.setOnClickListener {
            toggleCardView()
        }


        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Optional: Handle text change events
            }

            override fun afterTextChanged(editable: Editable?) {
                // Optional: Add any post-text-change logic here

            }
        })

    }

    private fun addMovableText(text: String) {
        val cardView = CardView(this)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.layoutParams = layoutParams

        val relativeLayout = RelativeLayout(this)
        val relativeLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        relativeLayout.layoutParams = relativeLayoutParams

        val textView = TextView(this)
        textView.text = text
        textView.textSize = 18f


        textView.setTextColor(resources.getColor(android.R.color.white))
        layoutParams.height = 150


        // Set text gravity to center
       // textView.gravity = Gravity.CENTER
        textView.setPadding(15,30,15,25)


        cardView.setCardBackgroundColor(resources.getColor(android.R.color.black))
        cardView.cardCornerRadius = resources.getDimension(R.dimen.card_corner_radius_text) // Set corner radius
        // Set RelativeLayout gravity to center


        relativeLayout.addView(textView)
        cardView.addView(relativeLayout)


        frameLayout.addView(cardView)
        makeCardViewMovable(cardView)
        relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
    }

    private fun toggleCardView() {
        if (!::cardView.isInitialized) {
            // Initialize the CardView
            cardView = CardView(this)

            // Replace "your_gif_url_here" with the URL of your GIF or provide the resource ID if it's a local resource.
            val gifUrl = "https://media1.tenor.com/m/NjavXXAMRD8AAAAC/sound.gif"

            val imageView = ImageView(this)

            // Use Glide to load the GIF into the ImageView
            Glide.with(this)
                .asGif()
                .load(gifUrl)
                .into(imageView)
            // Replace R.drawable.ic_sound_wave with your actual sound wave image resource
//            imageView.setImageResource(R.drawable.wave)
//            imageView.maxHeight = 5

            // Set a margin for the CardView
           val margin = resources.getDimensionPixelSize(R.dimen.card_margin)

            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.width = editText.width -8 * margin   // Adjust width for margin
            layoutParams.height = editText.height  // Match the height of the EditText card view

            cardView.cardCornerRadius = resources.getDimension(R.dimen.card_corner_radius)
            cardView.setCardBackgroundColor(resources.getColor(android.R.color.white))
            cardView.layoutParams = layoutParams

            cardView.addView(imageView)
            frameLayout.addView(cardView)
            makeCardViewMovable(cardView)
        } else {
            // Toggle visibility
            cardView.visibility = if (cardView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun makeCardViewMovable(cardView: CardView) {
        cardView.setOnTouchListener(object : View.OnTouchListener {
            var dX = 0f
            var dY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        startTextAnimation(view)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                    }
                    else -> return false
                }
                return true
            }
        })
    }

    private fun startTextAnimation(view: View) {
        val scaleUp = ScaleAnimation(
            1f, 1.5f,  // Scale from 1x to 1.5x in the X direction
            1f, 1.5f,  // Scale from 1x to 1.5x in the Y direction
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point for scaling in X (center)
            Animation.RELATIVE_TO_SELF, 0.5f   // Pivot point for scaling in Y (center)
        )

        scaleUp.duration = 1000
        scaleUp.repeatCount = 1
        scaleUp.repeatMode = Animation.REVERSE

        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        fadeIn.duration = 1000

        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        fadeOut.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.play(fadeIn).before(scaleUp)?.before(fadeOut)
        animatorSet.start()
    }




    private fun Unit.before(fadeOut: ObjectAnimator?) {

    }
    private fun AnimatorSet.Builder.before(scaleUp: ScaleAnimation) {

    }

}
