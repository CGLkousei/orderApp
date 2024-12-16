package com.example.web01.Service;

import com.example.web01.Entity.SeatEntity;
import com.example.web01.Repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

    //新しいシートを保存
    public SeatEntity saveSeat(SeatEntity seat){
        return seatRepository.save(seat);   // save() メソッドで保存
    }

    // IDでシートを検索
    public Optional<SeatEntity> getSeatById(Long id) {
        return seatRepository.findById(id);  // findById() メソッドで検索
    }

    // 全シートを取得
    public List<SeatEntity> getAllSeats() {
        return seatRepository.findAll();  // findAll() メソッドで全件取得
    }

    // IDでシートを削除
    public void deleteSeatById(Long id) {
        seatRepository.deleteById(id);  // deleteById() メソッドで削除
    }
}
