package net.sejuku.terakoya.deliverylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static net.sejuku.terakoya.deliverylist.DestinationDao.DestinationRecord;

@Controller
/**
 * 配達先一覧画面のコントローラークラス
 */
@RequestMapping(value = "/destination")
public class DestinationController {
    Logger logger = LoggerFactory.getLogger(DestinationController.class);
    private DestinationDao destinationDao;
    record DestinationEditForm(String destinationId, String destinationName, Boolean isEdit) {}

    /**
     * 配達先一覧で使用するクラスのインスタンス化
     * @param destinationDao　配達先のデータベース関連の処理をまとめたクラス
     */
    @Autowired
    DestinationController(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
    }

    /**
     * 画面表示処理
     * @param model　モデル
     * @return　表示画面のHTMLファイル
     */
    @GetMapping("/")
    public String index(Model model) {
        logger.debug("index in");
        model.addAttribute("destinationList", destinationDao.findAll());
        return "destination/index";
    }

    /**
     * 削除処理
     * @param id　削除する配達先のid
     * @return　表示画面のURL
     */
    @PostMapping("/delete")
    public String delete(@RequestParam String id) {
        logger.info("delete id is {}", id);
        destinationDao.delete(id);
        return "redirect:/destination/";
    }

    /**
     * 登録処理
     * @param form　配達先編集フォームの入力データの取得
     * @return　表示画面のURL
     */
    @PostMapping("/register")
    public String registry(@ModelAttribute DestinationEditForm form) {
        logger.debug("registry in {}", form.destinationId);
        if (form.isEdit) {
            destinationDao.update(new DestinationRecord(
                    Integer.parseInt(form.destinationId),
                    form.destinationName
            ));
        } else {
            destinationDao.insert(new DestinationRecord(
                    Integer.parseInt(form.destinationId),
                    form.destinationName
            ));
        }
        return "redirect:/destination/";
    }
}
