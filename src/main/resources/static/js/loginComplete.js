FIND.id('logout').on('click', async () => {
	const res = await new FindFetchRequest('/api/member/logout').credentials().authorization().send();
	
	console.log(res);
	
	responseHandler(res,
	() => {
		window.location.href = res.redirectUrl;
	},
	() => {
		alert(res.message);
	})
})