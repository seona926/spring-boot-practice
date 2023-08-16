package com.godcoder.myhome.controller;

import com.godcoder.myhome.model.Board;
import com.godcoder.myhome.repository.BoardRepository;
import com.godcoder.myhome.validator.BoardValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardValidator boardValidator;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 2) Pageable pageable, @RequestParam(required = false, defaultValue = "") String searchText) {
//        Page<Board> boards = boardRepository.findAll(pageable); 조건 없이 모든 검색에 쓰임
        // 아래는 제목 or 내용으로 검색
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
//        boards.getTotalElements(); 이걸로 총 게시글의 수를 얻는데, html에서 boards.totalElement의 형식으로 가져올 수 있음
        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
//        boards.getPageable().getPageNumber();
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards", boards);
        return "board/list";
    }

    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long id) { // id param 값이 필수는 아니지만 전달받는다면 id로 사용하겠다
        if(id == null) { // 글 새로 작성하는 경우
            model.addAttribute("board", new Board());
        } else {
            Board board = boardRepository.findById(id).orElse(null); // 조회된 id가 없으면 null을 준다
            model.addAttribute("board", board);
        }

        return "board/form";
    }

    @PostMapping("/form")
    public String greetingSubmit(@Valid Board board, BindingResult bindingResult) {
        boardValidator.validate(board, bindingResult);
        if (bindingResult.hasErrors()) {
            return "board/form";
        }
        boardRepository.save(board);
        return "redirect:/board/list"; // 작성 완료 후 리다이렉트 되며 리스트 페이지로 이동(새로운 내용 업데이트)
    }
}
