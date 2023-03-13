package com.starry.greenstash.ui.screens.backups

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.utils.getActivity

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun BackupScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = (context.getActivity() as MainActivity)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
                Text(
                    text = stringResource(id = R.string.backup_screen_header),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = null
                    )
                }
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            )
            )
        },
        content = {
            BackupScreenContent(
                paddingValues = it,
                onBackupClicked = { activity.backupDatabase() },
                onRestoreClicked = { activity.restoreDatabs() })
        })
}

@Composable
fun BackupScreenContent(
    paddingValues: PaddingValues,
    onBackupClicked: () -> Unit,
    onRestoreClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        val compositionResult: LottieCompositionResult = rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.backup_lottie)
        )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value,
            isPlaying = true,
            iterations = LottieConstants.IterateForever,
            speed = 1f
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            LottieAnimation(
                composition = compositionResult.value,
                progress = progressAnimation,
                modifier = Modifier.size(320.dp),
                enableMergePaths = true
            )
        }

        Text(
            text = stringResource(id = R.string.backup_screen_text),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        )

        Text(
            text = stringResource(id = R.string.backup_screen_sub_text),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onBackupClicked,
                modifier = Modifier
                    .padding(top = 70.dp, bottom = 16.dp)
                    .height(50.dp)
                    .fillMaxWidth(0.75f)
            ) {
                Text(
                    text = stringResource(id = R.string.backup_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onRestoreClicked,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.75f)
            ) {
                Text(
                    text = stringResource(id = R.string.restore_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.weight(2f))
    }
}