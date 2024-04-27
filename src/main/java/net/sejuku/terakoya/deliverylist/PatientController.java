package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static net.sejuku.terakoya.deliverylist.PatientDao.*;

@Controller
/**
 * 患者一覧画面のコントローラー
 */
@RequestMapping(value = "/patient")
public class PatientController {
    Logger logger = LoggerFactory.getLogger(PatientController.class);
    private PatientDao patientDao;
    record PatientEditForm(String patientId, String patientName, LocalDate patientBirthday, Boolean isEdit, String destinationId) {}

    /**
     * 患者一覧で使用するクラスのインスタンス化
     * @param patientDao　患者のデータベース関連の処理をまとめたクラス
     */
    @Autowired
    PatientController(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    /**
     * 画面表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　表示画面のHTMLファイル
     */
    @GetMapping("/{destinationId}")
    public String getIndex(@PathVariable(name="destinationId") String destinationId, Model model) {
        logger.debug("index get in");
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("destinationId", destinationId);
        return "patient/index";
    }

    /**
     * 処方記録画面から患者一覧画面への表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @PostMapping("/")
    public String postIndex(@RequestParam(name="destinationId") String destinationId, Model model) {
        logger.debug("index post in");
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("destinationId", destinationId);
        return "patient/index";
    }

    /**
     * 削除処理
     * @param id　削除する患者のid
     * @param destinationId　配達先のid
     * @return　表示画面のURL
     */
    @PostMapping("/delete")
    public String delete(@RequestParam String id, String destinationId) {
        logger.info("delete id is {}", id);
        patientDao.delete(id);
        return "redirect:/patient/" + destinationId;
    }

    /**
     * 編集画面表示処理
     * @param id　編集する患者のid
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @GetMapping("/edit")
    public String edit(@RequestParam String id, String destinationId, Model model) {
        logger.debug("edit in");
        model.addAttribute("isEdit", true);
        model.addAttribute("patient", patientDao.find(id));
        model.addAttribute("destinationId", destinationId);
        return "patient/edit";
    }

    /**
     * 新規登録画面表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @GetMapping("/new")
    public String new_entry(@RequestParam String destinationId, Model model) {
        logger.debug("new in");
        model.addAttribute("isEdit", false);
        model.addAttribute("patient", new PatientInfo(-1,"",null));
        model.addAttribute("destinationId", destinationId);
        return "patient/edit";
    }

    /**
     * 登録処理
     * @param form　患者編集フォームの入力データの取得
     * @return　表示画面のURL
     */
    @PostMapping("/register")
    public String registry(@ModelAttribute PatientEditForm form) {
        logger.debug("registry in {}", form.patientId);
        if (form.isEdit) {
            patientDao.update(new PatientRecord(
                    Integer.parseInt(form.patientId),
                    form.patientName,
                    form.patientBirthday
            ));
        } else {
            patientDao.insert(new PatientRecord(
                    Integer.parseInt(form.patientId),
                    form.patientName,
                    form.patientBirthday
            ));
        }
        return "redirect:/patient/" + form.destinationId;
    }
}
