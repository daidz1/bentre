package ws.core.module.ioffice;

import java.io.Serializable;
import java.util.Date;

import ws.core.util.DateTimeUtil;


@SuppressWarnings("serial")
public class VanbanModel implements Serializable{
	private int typeId=0;
	private int idVanban=0;
	private String idGoiTin=null;
	private String idIOffice=null;
	private int idDonvibanhanh=0;
	private String tenDonvibanhanh="";
	private int domat;
	private String soHieu="";
	private String kyHieu="";
	private Date ngayBanhanh=null;
	private int idLoaivanban=0;
	private String tenLoaivanban="";
	private String trichYeu="";
	private String noiDung="";
	private int idChucvuNguoiky=0;
	private String tenChucVuNguoiky="";
	private int idNguoiky=0;
	private String tenNguoiky="";
	private Date hanXuly=null;
	private String noiNhan="";
	private int doKhan=0;
	private int soLuongBanPhatHanh=1;
	private int soTrangCuaVanBan=1;
	private Date Ngaythem=null;
	private String UsernamNguoisoan="";
	private int UserIdNguoisoan=0;
	private String HoTenNguoiSoan="";
	private int IdDonvinhap=0;
	private String TenDonvinhap="";
	private int IdPhongsoan=0;
	private String TenPhongsoan="";
	private int TaskCount=0;

	public VanbanModel() {
		domat=0;
		typeId=TaskType.Vanbanphathanh.getId();
	}
	
	public int getTypeId() {
		return typeId;
	}
	public int getIdVanban() {
		return idVanban;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public int getId() {
		return idVanban;
	}
	public void setIdVanban(int idVanban) {
		this.idVanban = idVanban;
	}
	public String getIdGoiTin() {
		return idGoiTin;
	}
	public void setIdGoiTin(String idGoiTin) {
		this.idGoiTin = idGoiTin;
	}
	public String getIdIOffice() {
		return idIOffice;
	}
	public void setIdIOffice(String idVanbanIOffice) {
		this.idIOffice = idVanbanIOffice;
	}
	public int getIdDonvibanhanh() {
		return idDonvibanhanh;
	}
	public void setIdDonvibanhanh(int idDonvibanhanh) {
		this.idDonvibanhanh = idDonvibanhanh;
	}
	public String getTenDonvibanhanh() {
		return tenDonvibanhanh;
	}
	public void setTenDonvibanhanh(String tenDonvibanhanh) {
		this.tenDonvibanhanh = tenDonvibanhanh;
	}
	public Domat getDomat() {
		return Domat.getItem(domat);
	}
	public void setDomat(int domat) {
		this.domat = domat;
	}
	public String getSoHieu() {
		return soHieu;
	}
	public void setSoHieu(String soDi) {
		this.soHieu = soDi;
	}
	public String getKyHieu() {
		return kyHieu;
	}
	public void setKyHieu(String kyHieu) {
		this.kyHieu = kyHieu;
	}
	public Date getNgayBanhanh() {
		return ngayBanhanh;
	}
	public void setNgayBanhanh(Date ngayBanhanh) {
		this.ngayBanhanh = ngayBanhanh;
	}
	public int getIdLoaivanban() {
		return idLoaivanban;
	}
	public void setIdLoaivanban(int loaiVanban) {
		this.idLoaivanban = loaiVanban;
	}
	public String getTenLoaivanban() {
		return tenLoaivanban;
	}
	public void setTenLoaivanban(String tenLoaivanban) {
		this.tenLoaivanban = tenLoaivanban;
	}
	public String getTrichYeu() {
		return trichYeu;
	}
	public void setTrichYeu(String trichYeu) {
		this.trichYeu = trichYeu;
	}
	public String getNoiDung() {
		return noiDung;
	}
	public void setNoiDung(String noiDung) {
		this.noiDung = noiDung;
	}
	public int getIdChucvuNguoiky() {
		return idChucvuNguoiky;
	}
	public void setIdChucvuNguoiky(int idChucvuNguoiky) {
		this.idChucvuNguoiky = idChucvuNguoiky;
	}
	public String getTenChucVuNguoiky() {
		return tenChucVuNguoiky;
	}
	public void setTenChucVuNguoiky(String chucVunguoiky) {
		this.tenChucVuNguoiky = chucVunguoiky;
	}
	public int getIdNguoiky() {
		return idNguoiky;
	}
	public void setIdNguoiky(int idNguoiky) {
		this.idNguoiky = idNguoiky;
	}
	public String getTenNguoiky() {
		return tenNguoiky;
	}
	public void setTenNguoiky(String hoTenNguoiky) {
		this.tenNguoiky = hoTenNguoiky;
	}
	public Date getHanXuly() {
		return hanXuly;
	}
	public void setHanXuly(Date hanXuly) {
		this.hanXuly = hanXuly;
	}
	public String getNoiNhan() {
		return noiNhan;
	}
	public void setNoiNhan(String noiNhan) {
		this.noiNhan = noiNhan;
	}
	public int getDoKhan() {
		return doKhan;
	}
	public void setDoKhan(int doKhan) {
		this.doKhan = doKhan;
	}
	public Date getNgaythem() {
		return Ngaythem;
	}
	public void setNgaythem(Date ngaythem) {
		Ngaythem = ngaythem;
	}
	public String getUsernamNguoisoan() {
		return UsernamNguoisoan;
	}
	public void setUsernamNguoisoan(String usernamNguoisoan) {
		UsernamNguoisoan = usernamNguoisoan;
	}
	public int getUserIdNguoisoan() {
		return UserIdNguoisoan;
	}
	public void setUserIdNguoisoan(int userIdNguoisoan) {
		UserIdNguoisoan = userIdNguoisoan;
	}
	public int getTaskCount() {
		return TaskCount;
	}
	public void setTaskCount(int taskCount) {
		TaskCount = taskCount;
	}
	public int getIdDonvinhap() {
		return IdDonvinhap;
	}
	public void setIdDonvinhap(int idDonvinhap) {
		IdDonvinhap = idDonvinhap;
	}
	public String getTenDonvinhap() {
		return TenDonvinhap;
	}
	public void setTenDonvinhap(String tenDonvinhap) {
		TenDonvinhap = tenDonvinhap;
	}
	public int getIdPhongsoan() {
		return IdPhongsoan;
	}
	public void setIdPhongsoan(int idPhongsoan) {
		IdPhongsoan = idPhongsoan;
	}
	public String getTenPhongsoan() {
		return TenPhongsoan;
	}
	public void setTenPhongsoan(String tenPhongsoan) {
		TenPhongsoan = tenPhongsoan;
	}
	public String getHoTenNguoiSoan() {
		return HoTenNguoiSoan;
	}
	public void setHoTenNguoiSoan(String hoTenNguoiSoan) {
		HoTenNguoiSoan = hoTenNguoiSoan;
	}
	public String getNgayBanhanhText(){
		return DateTimeUtil.getDateFormat().format(ngayBanhanh);
	}
	public String getNgayThemText(){
		return DateTimeUtil.getDatetimeFormat().format(Ngaythem);
	}
	public int getSoLuongBanPhatHanh() {
		return soLuongBanPhatHanh;
	}
	public void setSoLuongBanPhatHanh(int soLuongBanPhatHanh) {
		this.soLuongBanPhatHanh = soLuongBanPhatHanh;
	}
	public int getSoTrangCuaVanBan() {
		return soTrangCuaVanBan;
	}
	public void setSoTrangCuaVanBan(int soTrangCuaVanBan) {
		this.soTrangCuaVanBan = soTrangCuaVanBan;
	}
}
