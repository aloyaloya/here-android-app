package ru.aloyaloya.onboarding.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HereTextButton
import ru.aloyaloya.design_system.component.pager.HerePagerIndicator
import ru.aloyaloya.design_system.component.permission.PermissionPage
import ru.aloyaloya.design_system.component.permission.PermissionScreen
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.onboarding.presentation.illustration.CalendarIllustration
import ru.aloyaloya.onboarding.presentation.illustration.MemoryIllustration
import ru.aloyaloya.onboarding.presentation.illustration.WelcomeIllustration

/**
 * Возможность приложения, о которой рассказывает онбординг.
 *
 * @property title Заголовок.
 * @property body Пояснение.
 */
enum class OnboardingFeature(@StringRes val title: Int, @StringRes val body: Int) {
    MAP(R.string.onboarding_map_title, R.string.onboarding_map_body),
    MEMORY(R.string.onboarding_memory_title, R.string.onboarding_memory_body),
    CALENDAR(R.string.onboarding_calendar_title, R.string.onboarding_calendar_body)
}

/**
 * Экран о возможности приложения.
 *
 * @param feature Возможность.
 * @param onNext Колбэк кнопки «Далее».
 * @param onSkip Колбэк кнопки «Пропустить».
 * @param onNavigateBack Колбэк кнопки «Назад».
 */
@Composable
fun FeatureScreen(
    feature: OnboardingFeature,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emotions = HereTheme.colors.emotions

    PermissionScreen(
        primaryText = stringResource(R.string.onboarding_next),
        onPrimaryClick = onNext,
        modifier = modifier,
        canNavigateBack = feature != OnboardingFeature.entries.first(),
        onNavigateBack = onNavigateBack,
        topAction = {
            HereTextButton(
                text = stringResource(R.string.onboarding_skip),
                onClick = onSkip
            )
        }
    ) { contentPadding ->
        PermissionPage(
            contentPadding = contentPadding,
            illustrationBackground = when (feature) {
                OnboardingFeature.MAP -> colorResource(R.color.onboarding_map_land)
                OnboardingFeature.MEMORY -> emotions.tender.soft
                OnboardingFeature.CALENDAR -> emotions.calm.soft
            },
            illustration = {
                when (feature) {
                    OnboardingFeature.MAP -> WelcomeIllustration()
                    OnboardingFeature.MEMORY -> MemoryIllustration()
                    OnboardingFeature.CALENDAR -> CalendarIllustration()
                }
            },
            title = stringResource(feature.title),
            body = stringResource(feature.body),
            pageIndicator = {
                HerePagerIndicator(pageCount = OnboardingSteps, currentPage = feature.ordinal)
            }
        )
    }
}
