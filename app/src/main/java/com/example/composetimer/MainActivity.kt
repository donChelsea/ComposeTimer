package com.example.composetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetimer.ui.theme.ComposeTimerTheme
import com.example.composetimer.ui.theme.LightBlue

class MainActivity : ComponentActivity() {
    private val timerViewModel by viewModels<TimerViewModel>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTimerTheme {
                TimerApp(timerViewModel = timerViewModel)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerApp(timerViewModel: TimerViewModel, modifier: Modifier = Modifier) {
    val secs = timerViewModel.seconds.collectAsState()
    val minutes = timerViewModel.minutes.collectAsState()
    val hours = timerViewModel.hours.collectAsState()
    val resumed = timerViewModel.isRunning.collectAsState()

    val progress = timerViewModel.progress.collectAsState(1f)
    val timeShow = timerViewModel.time.collectAsState(initial = "00:00:00")

    Surface(color = LightBlue) {
        val typography = MaterialTheme.typography

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Countdown Timer",
                    fontSize = 24.sp,
                    style = typography.h4,
                    color = Color.White,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(Modifier.padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.Yellow,
                        modifier = Modifier.size(250.dp),
                        progress = progress.value,
                        strokeWidth = 12.dp
                    )
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        HeaderText(
                            text = timeShow.value,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(4.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimerComponent(
                    value = hours.value,
                    timeUnit = TimerViewModel.Companion.TimeUnit.HOUR,
                    enabled = resumed.value != true
                ) {
                    timerViewModel.modifyTime(TimerViewModel.Companion.TimeUnit.HOUR, it)
                }

                Text(text = " : ", fontSize = 36.sp)

                TimerComponent(
                    value = minutes.value,
                    timeUnit = TimerViewModel.Companion.TimeUnit.MIN,
                    enabled = resumed.value != true
                ) {
                    timerViewModel.modifyTime(TimerViewModel.Companion.TimeUnit.MIN, it)
                }

                Text(text = " : ", fontSize = 36.sp)

                TimerComponent(
                    value = secs.value,
                    timeUnit = TimerViewModel.Companion.TimeUnit.SEC,
                    enabled = resumed.value != true
                ) {
                    timerViewModel.modifyTime(TimerViewModel.Companion.TimeUnit.SEC, it)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!(secs.value == 0 && minutes.value == 0 && hours.value == 0)) {
                            if (resumed.value != true) {
                                timerViewModel.startCountDown()
                            } else {
                                timerViewModel.cancelTimer()
                            }
                        } else null
                    },
                    modifier = modifier
                        .padding(16.dp)
                        .height(48.dp)
                        .widthIn(min = 48.dp),
                    backgroundColor = Color.Yellow,
                    contentColor = MaterialTheme.colors.onPrimary
                ) {
                    AnimatingFabContent(
                        icon = {
                            if (resumed.value != true)
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = null
                                ) else
                                Icon(
                                    imageVector = Icons.Outlined.Pause,
                                    contentDescription = null
                                )
                        },
                        text = {
                            if (resumed.value != true)
                                Text(
                                    color = Color.DarkGray,
                                    text = "Count Down!"
                                ) else
                                Text(
                                    color = Color.DarkGray,
                                    text = "Pause"
                                )
                        },
                        extended = true
                    )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerComponent(
    value: Int?,
    timeUnit: TimerViewModel.Companion.TimeUnit,
    enabled: Boolean,
    onClick: (TimerViewModel.Companion.TimeOperator) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = timeUnit.toString(),
            fontSize = 20.sp,
            color = Color.White,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 8.dp)
        )

        OperatorButton(
            timeOperator = TimerViewModel.Companion.TimeOperator.INCREASE,
            isEnabled = enabled,
            onClick = onClick,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = String.format("%02d", value ?: 0),
            fontSize = 32.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )

        OperatorButton(
            timeOperator = TimerViewModel.Companion.TimeOperator.DECREASE,
            isEnabled = enabled,
            onClick = onClick,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun OperatorButton(
    isEnabled: Boolean,
    timeOperator: TimerViewModel.Companion.TimeOperator,
    onClick: (TimerViewModel.Companion.TimeOperator) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isEnabled,
        modifier = modifier,
    ) {
        Button(
            onClick = { onClick.invoke(timeOperator) },
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                disabledBackgroundColor = MaterialTheme.colors.background
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            when (timeOperator) {
                TimerViewModel.Companion.TimeOperator.INCREASE -> Icon(
                    Icons.Outlined.ArrowDropUp,
                    null,
                    Modifier.size(24.dp)
                )
                TimerViewModel.Companion.TimeOperator.DECREASE -> Icon(
                    Icons.Outlined.ArrowDropDown,
                    null,
                    Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderText(text: String, color: Color) {
    Text(text = text, fontSize = 42.sp, textAlign = TextAlign.Center, style = MaterialTheme.typography.h1, color = color)
}

//@ExperimentalAnimationApi
//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//private fun previewTimer() {
//    TimerApp(timerViewModel = TimerViewModel())
//}

@ExperimentalAnimationApi
@Preview(
    showBackground = true
)
@Composable
private fun previewTimerComponent() {
    TimerComponent(
        value = 0,
        timeUnit = TimerViewModel.Companion.TimeUnit.SEC,
        enabled = true,
    ) {}
}