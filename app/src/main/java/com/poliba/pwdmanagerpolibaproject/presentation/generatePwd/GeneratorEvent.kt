package com.poliba.pwdmanagerpolibaproject.presentation.generatePwd

sealed class GeneratorEvent {
    data object OnGeneratePassword : GeneratorEvent()
    data class OnPasswordLengthChange(val length: Int) : GeneratorEvent()
    data class OnUppercaseChange(val include: Boolean) : GeneratorEvent()
    data class OnLowercaseChange(val include: Boolean) : GeneratorEvent()
    data class OnNumbersChange(val include: Boolean) : GeneratorEvent()
    data class OnSpecialCharsChange(val include: Boolean) : GeneratorEvent()
    data object OnCopyPassword : GeneratorEvent()
    data object OnCancelSave : GeneratorEvent()
    data class OnPasswordDataChange(val field: String, val value: String) : GeneratorEvent()
    data object OnSavePassword : GeneratorEvent()
}