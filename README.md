# Conversor de Moedas

App Android em Kotlin que converte valores entre moedas usando a cotação do dia.

- Converte entre Real, Dólar, Euro, Libra, Peso argentino, Dólar canadense, Franco suíço, Iene e Yuan.
- O resultado aparece enquanto você digita, e um botão inverte as moedas.
- As cotações vêm da API gratuita [open.er-api.com](https://www.exchangerate-api.com/docs/free), que não precisa de chave.
- Sem internet, o app usa a última cotação salva no aparelho.

## Como rodar

Abra o projeto no Android Studio e clique em **Run**, ou pelo terminal:

```bash
./gradlew assembleDebug        # gera o APK
./gradlew testDebugUnitTest    # roda os testes de conversão
```
