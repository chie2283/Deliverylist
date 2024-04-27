package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static net.sejuku.terakoya.deliverylist.PrescriptionDao.PrescriptionInfo;
import static net.sejuku.terakoya.deliverylist.PrescriptionDao.PrescriptionRecord;

@Controller
/**
 * 処方記録の画面のコントローラー
 */
@RequestMapping(value = "/prescription")
public class PrescriptionController {
    Logger logger = LoggerFactory.getLogger(PrescriptionController.class);
    private PrescriptionDao prescriptionDao;
    private DestinationDao destinationDao;
    private PatientDao patientDao;

    private EnteralNutrientDao enteralNutrientDao;
    record PrescriptionEditForm(String id,
                                String destinationId,
                                String patientId,
                                String enteralNutrientId,
                                String dosage,
                                LocalDate dt,
                                Integer days,
                                LocalDate startDate,
                                LocalDate deliveryDt,
                                Integer doneDays,
                                Boolean done,
                                Boolean isEdit) {}

    /**
     * 処方記録で使用するクラスのインスタンス化
     * @param prescriptionDao　処方のデータベース関連の処理をまとめたクラス
     * @param destinationDao　配達先のデータベース関連の処理をまとめたクラス
     * @param patientDao　患者のデータベース関連の処理をまとめたクラス
     * @param enteralNutrientDao　栄養剤のデータベース関連の処理をまとめたクラス
     */
    @Autowired
    PrescriptionController(PrescriptionDao prescriptionDao, DestinationDao destinationDao, PatientDao patientDao, EnteralNutrientDao enteralNutrientDao) {
        this.prescriptionDao = prescriptionDao;
        this.destinationDao = destinationDao;
        this.patientDao = patientDao;
        this.enteralNutrientDao = enteralNutrientDao;
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
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("destinationName", destinationDao.find(destinationId).name());
        model.addAttribute("prescriptionList", prescriptionDao.findDestinationId(destinationId));
        return "prescription/index";
    }

    /**
     * 配達先一覧画面・カレンダー画面から処方記録画面への表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @PostMapping("/")
    public String postIndex(@RequestParam(name="destinationId") String destinationId, Model model) {
        logger.debug("index post in");
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("destinationName", destinationDao.find(destinationId).name());
        model.addAttribute("prescriptionList", prescriptionDao.findDestinationId(destinationId));
        return "prescription/index";
    }

    /**
     * 削除処理
     * @param id　削除する処方記録のid
     * @param destinationId　配達先のid
     * @return　表示画面のURL
     */
    @PostMapping("/delete")
    public String delete(@RequestParam String id, String destinationId) {
        logger.info("delete id is {}", id);
        prescriptionDao.delete(id);
        return "redirect:/prescription/" + destinationId;
    }

    /**
     * 編集画面表示処理
     * @param id　編集する処方記録のid
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @GetMapping("/edit")
    public String edit(@RequestParam String id, String destinationId, Model model) {
        logger.debug("edit in");
        model.addAttribute("isEdit", true);
        model.addAttribute("prescription", prescriptionDao.find(id));
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("destinationName", prescriptionDao.find(id).destinationName());
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("enteralNutrientList", enteralNutrientDao.findAll());
        return "prescription/edit";
    }

    /**
     * 新規登録画面表示処理
     * @param destinationId　配達先のid
     * @param model　モデル
     * @return　遷移先のHTMLファイル
     */
    @GetMapping("/new")
    public String new_entry(@RequestParam(name="destinationId") String destinationId, Model model) {
        logger.debug("new in");
        model.addAttribute("isEdit", false);
        model.addAttribute("prescription", new PrescriptionInfo(-1,Integer.parseInt(destinationId),"",-1,"",-1,
                "","", null,14,null,null,null,14,false));
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("destinationName", destinationDao.find(destinationId).name());
        model.addAttribute("patientList", patientDao.findAll());
        model.addAttribute("enteralNutrientList", enteralNutrientDao.findAll());
        return "prescription/edit";
    }

    /**
     * 登録処理
     * @param form　処方記録フォームの入力データの取得
     * @return　表示画面のURL
     */
    @PostMapping("/register")
    public String registry(@ModelAttribute PrescriptionEditForm form) {
        logger.debug("registry in {}", form.id);
        if (form.isEdit) {
            prescriptionDao.update(new PrescriptionRecord(
                    Integer.parseInt(form.id),
                    form.destinationId,
                    form.patientId,
                    form.enteralNutrientId,
                    form.dosage,
                    form.dt,
                    form.days,
                    form.startDate,
                    form.startDate.plusDays(form.days),
                    form.deliveryDt,
                    form.doneDays,
                    form.done
            ));
        } else {
            prescriptionDao.insert(new PrescriptionRecord(
                    -1,
                    form.destinationId,
                    form.patientId,
                    form.enteralNutrientId,
                    form.dosage,
                    form.dt,
                    form.days,
                    form.startDate,
                    form.startDate.plusDays(form.days),
                    form.deliveryDt,
                    form.doneDays,
                    form.done
            ));
        }
        return "redirect:/prescription/" + form.destinationId;
    }
}
