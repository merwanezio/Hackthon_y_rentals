package com.mustfaibra.shoesstore.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustfaibra.shoesstore.R
import com.mustfaibra.shoesstore.components.CustomButton
import com.mustfaibra.shoesstore.components.CustomInputField
import com.mustfaibra.shoesstore.sealed.UiState
import com.mustfaibra.shoesstore.ui.theme.Dimension

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by remember { loginViewModel.uiState }
    val emailOrPhone by remember { loginViewModel.emailOrPhone }
    val password by remember { loginViewModel.password }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimension.pagePadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.primary,
        )
        Spacer(modifier = Modifier.height(Dimension.pagePadding))

        /** Login info input section */
        CustomInputField(
            modifier = Modifier
                .fillMaxWidth(),
            value = emailOrPhone ?: "",
            onValueChange = {
                loginViewModel.updateEmailOrPhone(value = it.ifBlank { null })
            },
            placeholder = "Email or Phone ...",
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Search,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .padding(end = Dimension.pagePadding.div(2))
                        .size(Dimension.mdIcon.times(0.7f)),
                    painter = painterResource(id = R.drawable.ic_profile_empty),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            },
            onFocusChange = { },
            onKeyboardActionClicked = { },
        )
        Spacer(modifier = Modifier.height(Dimension.pagePadding))
        CustomInputField(
            modifier = Modifier
                .fillMaxWidth(),
            value = password ?: "",
            onValueChange = {
                loginViewModel.updatePassword(value = it.ifBlank { null })
            },
            placeholder = "Password ...",
            visualTransformation = PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            padding = PaddingValues(
                horizontal = Dimension.pagePadding,
                vertical = Dimension.pagePadding.times(0.7f),
            ),
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onBackground,
            imeAction = ImeAction.Search,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .padding(end = Dimension.pagePadding.div(2))
                        .size(Dimension.mdIcon.times(0.7f)),
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                )
            },
            onFocusChange = { },
            onKeyboardActionClicked = { },
        )
        /** The login button */
        Spacer(modifier = Modifier.height(Dimension.pagePadding))
        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            padding = PaddingValues(Dimension.pagePadding.div(2)),
            buttonColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            text = stringResource(id = R.string.login),
            enabled = uiState !is UiState.Loading,
            textStyle = MaterialTheme.typography.button,
            onButtonClicked = {
                /** Handle the click event of the login button */
                loginViewModel.authenticateUser(
                    emailOrPhone = emailOrPhone ?: "",
                    password = password ?: "",
                    onAuthenticated = {
                        /** When user is authenticated, go home or back */

                    },
                    onAuthenticationFailed = {
                        /** Do whatever you want when it failed */
                    }
                )
            },
            leadingIcon = {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = Dimension.pagePadding)
                            .size(Dimension.smIcon),
                        color = MaterialTheme.colors.onPrimary,
                        strokeWidth = Dimension.xs
                    )
                }
            }
        )
    }
}