# FlowGrid - Android Native

Este é o código-fonte Android nativo completo do jogo **FlowGrid**, desenvolvido em Kotlin usando Jetpack Compose e arquitetura MVVM, conforme solicitado.

Para executar o projeto:
1. Faça o download da pasta `android/`. (Pode usar a opção de exportar ou baixar como ZIP)
2. Abra o Android Studio (Hedgehog ou superior).
3. Selecione a opção **Open** e navegue até a pasta `android` extraída.
4. Aguarde o Gradle sincronizar as dependências e faça o build.

Todos os componentes nativos estão inclusos:
- **UI:** Jetpack Compose (HomeScreen, GameScreen, VictoryScreen, SettingsScreen).
- **Engine:** Lógica procedural BFS e determinística convertida puramente para Kotlin (`LevelGenerator`, `PathValidator`).
- **Persistência:** Room Database para estatísticas de nível resolvido, e DataStorePreferences para controle de settings, modo daltônico e streak diário.
- **Injeção de dependências:** Configurado com Dagger-Hilt.
- **Deep Linking:** Intent-filter configurado em `AndroidManifest.xml` para rotas "flowgrid://challenge".
- **Haptic Feedback:** Integração com Vibrator/VibratorManager.
- **Compatibilidade AdMob e Billing:** Dependências e permissões prontas no manifest e `build.gradle.kts`.

*Nota:* O AI Studio é um ambiente nativamente web (Node.js/Vite para preview), então o preview visual continuará mostrando o app React web. O código Android pode ser baixado e executado perfeitamente na sua máquina real!
