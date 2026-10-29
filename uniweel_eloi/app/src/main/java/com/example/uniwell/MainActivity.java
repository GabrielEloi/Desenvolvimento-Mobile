package com.example.uniwell;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayDeque;
import java.util.Deque;

/** Navegação simples entre as telas XML preservadas do protótipo. */
public class MainActivity extends AppCompatActivity {
    private final Deque<Integer> history = new ArrayDeque<>();
    private FrameLayout host;
    private int currentScreen = R.layout.tela_13_hidratacao;
    private int remainingClassSlots = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Os layouts foram desenhados com coordenadas próprias para a área útil do app.
        // Ajustar ao sistema aqui evita que o conteúdo invada a barra de status.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(242, 247, 245));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(242, 247, 245));
        host = new FrameLayout(this);
        host.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(host);
        show(currentScreen, false);
    }

    private void show(int screen, boolean addHistory) {
        if (addHistory && currentScreen != screen) history.push(currentScreen);
        currentScreen = screen;
        host.removeAllViews();
        View page = getLayoutInflater().inflate(screen, host, false);
        host.addView(page);
        bind(page);
    }

    private void bind(View view) {
        if (view instanceof TextView) {
            TextView label = (TextView) view;
            String text = label.getText() == null ? "" : label.getText().toString().trim();
            if (currentScreen == R.layout.tela_25_detalhes_aula && text.matches("\\d+ vagas")) {
                label.setText(remainingClassSlots + (remainingClassSlots == 1 ? " vaga" : " vagas"));
                return;
            }
            int destination = destinationFor(text);
            if (currentScreen == R.layout.tela_25_detalhes_aula && text.equals("Inscrever-se")) {
                label.setOnClickListener(v -> {
                    if (remainingClassSlots > 0) {
                        remainingClassSlots--;
                        show(R.layout.tela_26_confirmacao_inscricao, true);
                    } else {
                        show(R.layout.tela_27_aula_lotada, true);
                    }
                });
                label.setClickable(true);
            } else if (destination != 0) {
                label.setOnClickListener(v -> show(destination, true));
                label.setClickable(true);
            }
            if (currentScreen == R.layout.tela_15_registrar_agua) {
                if (text.equals("+") || text.equals("−")) {
                    label.setOnClickListener(v -> adjustAmount(view, text.equals("+") ? 100 : -100));
                } else if (text.equals("200 ml") || text.equals("300 ml") || text.equals("500 ml")) {
                    label.setOnClickListener(v -> selectAmount(view, text));
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) bind(group.getChildAt(i));
        }
    }

    private int destinationFor(String text) {
        if (currentScreen == R.layout.tela_13_hidratacao) {
            if (text.equals("Registrar água")) return R.layout.tela_15_registrar_agua;
            if (text.equals("Ver histórico")) return R.layout.tela_16_historico_hidratacao;
            if (text.equals("Configurar lembretes")) return R.layout.tela_14_lembretes_hidratacao;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
        } else if (currentScreen == R.layout.tela_14_lembretes_hidratacao) {
            if (text.equals("‹") || text.equals("Salvar configurações") || text.equals("Início")) return R.layout.tela_13_hidratacao;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
        } else if (currentScreen == R.layout.tela_15_registrar_agua) {
            if (text.equals("‹") || text.equals("Início")) return R.layout.tela_13_hidratacao;
            if (text.equals("Registrar consumo")) return R.layout.tela_17_meta_atingida;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
        } else if (currentScreen == R.layout.tela_16_historico_hidratacao) {
            if (text.equals("‹") || text.equals("Início")) return R.layout.tela_13_hidratacao;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
        } else if (currentScreen == R.layout.tela_17_meta_atingida) {
            if (text.equals("‹") || text.equals("Registrar mais água")) return R.layout.tela_15_registrar_agua;
            if (text.equals("Ver Histórico")) return R.layout.tela_16_historico_hidratacao;
            if (text.equals("Início")) return R.layout.tela_13_hidratacao;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
        } else if (currentScreen == R.layout.tela_23_aulas) {
            if (text.equals("Ver todas")) return R.layout.tela_24_lista_aulas;
            if (text.equals("Minhas inscrições")) return R.layout.tela_28_minhas_inscricoes;
            if (isClassName(text)) return R.layout.tela_25_detalhes_aula;
            if (text.equals("Hidratação")) return R.layout.tela_13_hidratacao;
        } else if (currentScreen == R.layout.tela_24_lista_aulas) {
            if (text.equals("‹")) return R.layout.tela_23_aulas;
            if (isClassName(text)) return R.layout.tela_25_detalhes_aula;
            if (text.equals("Início")) return R.layout.tela_13_hidratacao;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
            if (text.equals("Hidratação")) return R.layout.tela_13_hidratacao;
        } else if (currentScreen == R.layout.tela_25_detalhes_aula) {
            if (text.equals("‹")) return R.layout.tela_24_lista_aulas;
            if (text.equals("Inscrever-se")) return R.layout.tela_26_confirmacao_inscricao;
            if (text.equals("Início")) return R.layout.tela_13_hidratacao;
            if (text.equals("Agenda")) return R.layout.tela_23_aulas;
            if (text.equals("Hidratação")) return R.layout.tela_13_hidratacao;
        } else if (currentScreen == R.layout.tela_26_confirmacao_inscricao) {
            if (text.equals("‹")) return R.layout.tela_25_detalhes_aula;
            if (text.equals("Ver minhas inscrições")) return R.layout.tela_28_minhas_inscricoes;
            if (text.equals("Ver outras aulas")) return R.layout.tela_24_lista_aulas;
        } else if (currentScreen == R.layout.tela_27_aula_lotada) {
            if (text.equals("‹")) return R.layout.tela_25_detalhes_aula;
            if (text.equals("Ver outras aulas")) return R.layout.tela_24_lista_aulas;
        } else if (currentScreen == R.layout.tela_28_minhas_inscricoes) {
            if (text.equals("‹")) return R.layout.tela_23_aulas;
            if (isClassName(text)) return R.layout.tela_29_detalhes_inscricao;
        } else if (currentScreen == R.layout.tela_29_detalhes_inscricao) {
            if (text.equals("‹")) return R.layout.tela_28_minhas_inscricoes;
            if (text.equals("Ver outras aulas")) return R.layout.tela_24_lista_aulas;
        }
        return 0;
    }

    private boolean isClassName(String text) {
        return text.equals("Pilates") || text.equals("Funcional") || text.equals("Boxe")
                || text.equals("Yoga") || text.equals("Muay Thai");
    }

    private void selectAmount(View source, String amount) {
        View page = host.getChildAt(0);
        TextView custom = page.findViewById(R.id.custom_water_amount);
        if (custom != null) custom.setText(amount.replace(" ml", ""));
    }

    private void adjustAmount(View source, int change) {
        View page = host.getChildAt(0);
        TextView amount = page.findViewById(R.id.custom_water_amount);
        if (amount == null) return;
        try { amount.setText(String.valueOf(Math.max(100, Integer.parseInt(amount.getText().toString()) + change))); }
        catch (NumberFormatException ignored) { }
    }

    private TextView findText(View view, String value) {
        if (view instanceof TextView && value.contentEquals(((TextView) view).getText())) return (TextView) view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                TextView found = findText(group.getChildAt(i), value);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (!history.isEmpty()) {
            currentScreen = history.pop();
            show(currentScreen, false);
        } else if (currentScreen != R.layout.tela_13_hidratacao) {
            show(R.layout.tela_13_hidratacao, false);
        } else {
            super.onBackPressed();
        }
    }
}
